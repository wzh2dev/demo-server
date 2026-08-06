package com.demo.server.payment.repository;

import com.demo.server.payment.domain.Payment;
import com.demo.server.payment.domain.PaymentChannel;
import com.demo.server.payment.domain.PaymentStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 支付仓储，内存实现。
 */
public class PaymentRepository {

    private final Map<Long, Payment> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0L);

    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(idSeq.incrementAndGet());
            if (payment.getTradeNo() == null) {
                payment.setTradeNo("T" + System.currentTimeMillis() + payment.getId());
            }
            long now = System.currentTimeMillis();
            payment.setCreatedAt(now);
            payment.setUpdatedAt(now);
            if (payment.getExpiredAt() == null) {
                // 默认 30 分钟过期
                payment.setExpiredAt(now + 30L * 60 * 1000);
            }
        } else {
            payment.setUpdatedAt(System.currentTimeMillis());
        }
        store.put(payment.getId(), payment);
        return payment;
    }

    public Payment findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public Payment findByOrderId(Long orderId) {
        if (orderId == null) {
            return null;
        }
        return store.values().stream()
                .filter(p -> orderId.equals(p.getOrderId()))
                .findFirst()
                .orElse(null);
    }

    public Payment findByTradeNo(String tradeNo) {
        if (tradeNo == null) {
            return null;
        }
        return store.values().stream()
                .filter(p -> tradeNo.equals(p.getTradeNo()))
                .findFirst()
                .orElse(null);
    }

    public List<Payment> findAll(PaymentChannel channel, PaymentStatus status,
                                 int pageNumber, int pageSize) {
        return store.values().stream()
                .filter(p -> channel == null || channel == p.getChannel())
                .filter(p -> status == null || status == p.getStatus())
                .sorted(Comparator.comparing(Payment::getCreatedAt,
                        Comparator.nullsFirst(Comparator.reverseOrder())))
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public long count(PaymentChannel channel, PaymentStatus status) {
        return store.values().stream()
                .filter(p -> channel == null || channel == p.getChannel())
                .filter(p -> status == null || status == p.getStatus())
                .count();
    }

    public List<Payment> findExpired(long now) {
        return store.values().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                .filter(p -> p.getExpiredAt() != null && p.getExpiredAt() <= now)
                .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}
