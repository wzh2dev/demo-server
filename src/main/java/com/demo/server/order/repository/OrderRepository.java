package com.demo.server.order.repository;

import com.demo.server.order.domain.Order;
import com.demo.server.order.domain.OrderStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 订单仓储，内存实现。
 */
public class OrderRepository {

    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0L);

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idSeq.incrementAndGet());
            if (order.getOrderNo() == null) {
                order.setOrderNo("NO" + System.currentTimeMillis() + order.getId());
            }
            long now = System.currentTimeMillis();
            order.setCreatedAt(now);
            order.setUpdatedAt(now);
        } else {
            order.setUpdatedAt(System.currentTimeMillis());
        }
        store.put(order.getId(), order);
        return order;
    }

    public Order findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public List<Order> findAll(Long userId, OrderStatus status, int pageNumber, int pageSize) {
        return store.values().stream()
                .filter(o -> userId == null || userId.equals(o.getUserId()))
                .filter(o -> status == null || status == o.getStatus())
                .sorted(Comparator.comparing(Order::getCreatedAt,
                        Comparator.nullsFirst(Comparator.reverseOrder())))
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public long count(Long userId, OrderStatus status) {
        return store.values().stream()
                .filter(o -> userId == null || userId.equals(o.getUserId()))
                .filter(o -> status == null || status == o.getStatus())
                .count();
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    public List<Order> findByUserId(Long userId) {
        return store.values().stream()
                .filter(o -> userId != null && userId.equals(o.getUserId()))
                .collect(Collectors.toList());
    }
}
