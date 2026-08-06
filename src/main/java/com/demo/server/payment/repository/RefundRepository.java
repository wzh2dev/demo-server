package com.demo.server.payment.repository;

import com.demo.server.payment.domain.Refund;
import com.demo.server.payment.domain.RefundStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 退款仓储，内存实现。
 */
public class RefundRepository {

    private final Map<Long, Refund> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0L);

    public Refund save(Refund refund) {
        if (refund.getId() == null) {
            refund.setId(idSeq.incrementAndGet());
            if (refund.getRefundNo() == null) {
                refund.setRefundNo("R" + System.currentTimeMillis() + refund.getId());
            }
            long now = System.currentTimeMillis();
            refund.setCreatedAt(now);
            refund.setUpdatedAt(now);
        } else {
            refund.setUpdatedAt(System.currentTimeMillis());
        }
        store.put(refund.getId(), refund);
        return refund;
    }

    public Refund findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public List<Refund> findByPaymentId(Long paymentId) {
        if (paymentId == null) {
            return java.util.Collections.emptyList();
        }
        return store.values().stream()
                .filter(r -> paymentId.equals(r.getPaymentId()))
                .collect(Collectors.toList());
    }

    public List<Refund> findAll(RefundStatus status, int pageNumber, int pageSize) {
        return store.values().stream()
                .filter(r -> status == null || status == r.getStatus())
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public long count(RefundStatus status) {
        return store.values().stream()
                .filter(r -> status == null || status == r.getStatus())
                .count();
    }

    public double sumRefundedAmountByPayment(Long paymentId) {
        if (paymentId == null) {
            return 0d;
        }
        return store.values().stream()
                .filter(r -> paymentId.equals(r.getPaymentId()))
                .filter(r -> r.getStatus() == RefundStatus.SUCCESS)
                .mapToDouble(Refund::getAmount)
                .sum();
    }
}
