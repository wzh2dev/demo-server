package com.demo.server.inventory.service;

import com.demo.server.inventory.domain.Inventory;
import com.demo.server.inventory.domain.InventoryChangeType;
import com.demo.server.inventory.domain.InventoryLog;
import com.demo.server.inventory.dto.InventoryAdjustRequest;
import com.demo.server.inventory.dto.InventoryLockRequest;
import com.demo.server.inventory.exception.NotFoundException;
import com.demo.server.inventory.exception.ValidationException;
import com.demo.server.inventory.repository.InventoryRepository;

import java.util.List;
import java.util.Objects;

/**
 * 库存领域服务。
 */
public class InventoryService {

    private static final int REASON_MAX_LEN = 200;

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = Objects.requireNonNull(inventoryRepository);
    }

    /**
     * 查询库存。
     */
    public Inventory get(Long skuId) {
        Inventory inventory = inventoryRepository.findBySkuId(skuId);
        if (inventory == null) {
            throw new NotFoundException("inventory", skuId);
        }
        return inventory;
    }

    /**
     * 初始化库存。
     */
    public Inventory init(Long skuId, int available) {
        if (skuId == null) {
            throw new ValidationException("skuId is null");
        }
        if (available < 0) {
            throw new ValidationException("available must be >= 0");
        }
        Inventory inventory = new Inventory(skuId, available, 0);
        inventory = inventoryRepository.saveIfAbsent(inventory);
        log(inventory.getSkuId(), available, InventoryChangeType.INBOUND, "init", null, null);
        return inventory;
    }

    /**
     * 入库。
     */
    public Inventory inbound(Long skuId, int quantity, String reason) {
        if (quantity <= 0) {
            throw new ValidationException("quantity must be > 0");
        }
        Inventory inventory = get(skuId);
        inventory.setAvailable(inventory.getAvailable() + quantity);
        inventoryRepository.save(inventory);
        log(skuId, quantity, InventoryChangeType.INBOUND, "inbound", null, reason);
        return inventory;
    }

    /**
     * 出库。
     */
    public Inventory outbound(Long skuId, int quantity, String reason) {
        if (quantity <= 0) {
            throw new ValidationException("quantity must be > 0");
        }
        Inventory inventory = get(skuId);
        if (inventory.getAvailable() < quantity) {
            throw new ValidationException("insufficient available stock");
        }
        inventory.setAvailable(inventory.getAvailable() - quantity);
        inventoryRepository.save(inventory);
        log(skuId, -quantity, InventoryChangeType.OUTBOUND, "outbound", null, reason);
        return inventory;
    }

    /**
     * 锁定库存（下单时）。
     */
    public Inventory lock(InventoryLockRequest req) {
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getQuantity() <= 0) {
            throw new ValidationException("quantity must be > 0");
        }
        Inventory inventory = get(req.getSkuId());
        if (inventory.getAvailable() < req.getQuantity()) {
            throw new ValidationException("insufficient available stock to lock");
        }
        inventory.setAvailable(inventory.getAvailable() - req.getQuantity());
        inventory.setLocked(inventory.getLocked() + req.getQuantity());
        inventoryRepository.save(inventory);
        log(req.getSkuId(), req.getQuantity(), InventoryChangeType.LOCK,
                req.getRefType(), req.getRefId(), "lock");
        return inventory;
    }

    /**
     * 解锁库存（订单取消时）。
     */
    public Inventory unlock(Long skuId, int quantity, String refType, String refId) {
        if (quantity <= 0) {
            throw new ValidationException("quantity must be > 0");
        }
        Inventory inventory = get(skuId);
        if (inventory.getLocked() < quantity) {
            throw new ValidationException("insufficient locked stock to unlock");
        }
        inventory.setLocked(inventory.getLocked() - quantity);
        inventory.setAvailable(inventory.getAvailable() + quantity);
        inventoryRepository.save(inventory);
        log(skuId, quantity, InventoryChangeType.UNLOCK, refType, refId, "unlock");
        return inventory;
    }

    /**
     * 扣减已锁定库存（发货时）。
     */
    public Inventory deductLocked(Long skuId, int quantity, String refType, String refId) {
        if (quantity <= 0) {
            throw new ValidationException("quantity must be > 0");
        }
        Inventory inventory = get(skuId);
        if (inventory.getLocked() < quantity) {
            throw new ValidationException("insufficient locked stock to deduct");
        }
        inventory.setLocked(inventory.getLocked() - quantity);
        inventoryRepository.save(inventory);
        log(skuId, -quantity, InventoryChangeType.OUTBOUND, refType, refId, "deduct locked");
        return inventory;
    }

    /**
     * 手工调整库存。
     */
    public Inventory adjust(Long skuId, InventoryAdjustRequest req) {
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getDelta() == 0) {
            throw new ValidationException("delta is zero");
        }
        if (req.getReason() != null && req.getReason().length() > REASON_MAX_LEN) {
            throw new ValidationException("reason too long");
        }
        Inventory inventory = get(skuId);
        int newAvailable = inventory.getAvailable() + req.getDelta();
        if (newAvailable < 0) {
            throw new ValidationException("available would be negative");
        }
        inventory.setAvailable(newAvailable);
        inventoryRepository.save(inventory);
        log(skuId, req.getDelta(), InventoryChangeType.ADJUST, "manual", null, req.getReason());
        return inventory;
    }

    /**
     * 查询库存变更流水。
     */
    public List<InventoryLog> listLogs(Long skuId) {
        return inventoryRepository.findLogsBySku(skuId);
    }

    /**
     * 查询低库存预警。
     */
    public List<Inventory> listLowStock(int threshold) {
        if (threshold < 0) {
            threshold = 10;
        }
        return inventoryRepository.findLowStock(threshold);
    }

    private void log(Long skuId, int delta, InventoryChangeType type,
                     String refType, String refId, String reason) {
        InventoryLog log = new InventoryLog();
        log.setSkuId(skuId);
        log.setDelta(delta);
        log.setType(type);
        log.setRefType(refType);
        log.setRefId(refId);
        log.setReason(reason);
        inventoryRepository.saveLog(log);
    }
}
