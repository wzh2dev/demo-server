package com.demo.server.inventory.repository;

import com.demo.server.inventory.domain.Inventory;
import com.demo.server.inventory.domain.InventoryLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 库存仓储，内存实现。
 */
public class InventoryRepository {

    private final Map<Long, Inventory> store = new ConcurrentHashMap<>();
    private final Map<Long, InventoryLog> logs = new ConcurrentHashMap<>();
    private final AtomicLong logIdSeq = new AtomicLong(0L);

    public Inventory findBySkuId(Long skuId) {
        if (skuId == null) {
            return null;
        }
        return store.get(skuId);
    }

    public Inventory save(Inventory inventory) {
        inventory.setUpdatedAt(System.currentTimeMillis());
        store.put(inventory.getSkuId(), inventory);
        return inventory;
    }

    public Inventory saveIfAbsent(Inventory inventory) {
        return store.computeIfAbsent(inventory.getSkuId(), k -> {
            inventory.setUpdatedAt(System.currentTimeMillis());
            return inventory;
        });
    }

    public List<Inventory> findAll() {
        return new ArrayList<>(store.values());
    }

    public InventoryLog saveLog(InventoryLog log) {
        if (log.getId() == null) {
            log.setId(logIdSeq.incrementAndGet());
            log.setCreatedAt(System.currentTimeMillis());
        }
        logs.put(log.getId(), log);
        return log;
    }

    public List<InventoryLog> findLogsBySku(Long skuId) {
        if (skuId == null) {
            return java.util.Collections.emptyList();
        }
        return logs.values().stream()
                .filter(l -> skuId.equals(l.getSkuId()))
                .sorted((a, b) -> Long.compare(
                        b.getCreatedAt() == null ? 0L : b.getCreatedAt(),
                        a.getCreatedAt() == null ? 0L : a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<Inventory> findLowStock(int threshold) {
        return store.values().stream()
                .filter(i -> i.getAvailable() <= threshold)
                .collect(Collectors.toList());
    }
}
