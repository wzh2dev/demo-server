package com.demo.server.cart.repository;

import com.demo.server.cart.domain.CartItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 购物车仓储，内存实现。
 */
public class CartRepository {

    private final Map<Long, CartItem> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0L);

    public CartItem save(CartItem item) {
        if (item.getId() == null) {
            item.setId(idSeq.incrementAndGet());
            long now = System.currentTimeMillis();
            item.setAddedAt(now);
            item.setUpdatedAt(now);
        } else {
            item.setUpdatedAt(System.currentTimeMillis());
        }
        store.put(item.getId(), item);
        return item;
    }

    public CartItem findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public CartItem findByUserAndProduct(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return null;
        }
        return store.values().stream()
                .filter(c -> userId.equals(c.getUserId()))
                .filter(c -> productId.equals(c.getProductId()))
                .findFirst()
                .orElse(null);
    }

    public List<CartItem> findByUser(Long userId, Boolean selected) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return store.values().stream()
                .filter(c -> userId.equals(c.getUserId()))
                .filter(c -> selected == null || c.isSelected() == selected)
                .sorted(Comparator.comparing(CartItem::getAddedAt,
                        Comparator.nullsFirst(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public int countByUser(Long userId) {
        if (userId == null) {
            return 0;
        }
        return (int) store.values().stream()
                .filter(c -> userId.equals(c.getUserId()))
                .count();
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    public int deleteByUser(Long userId) {
        if (userId == null) {
            return 0;
        }
        List<Long> toRemove = store.values().stream()
                .filter(c -> userId.equals(c.getUserId()))
                .map(CartItem::getId)
                .collect(Collectors.toList());
        for (Long id : toRemove) {
            store.remove(id);
        }
        return toRemove.size();
    }

    public int deleteSelectedByUser(Long userId) {
        if (userId == null) {
            return 0;
        }
        List<Long> toRemove = store.values().stream()
                .filter(c -> userId.equals(c.getUserId()))
                .filter(CartItem::isSelected)
                .map(CartItem::getId)
                .collect(Collectors.toList());
        for (Long id : toRemove) {
            store.remove(id);
        }
        return toRemove.size();
    }
}
