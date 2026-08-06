package com.demo.server.order.domain;

/**
 * 订单状态。
 */
public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    SHIPPED,
    COMPLETED,
    CANCELLED,
    REFUNDED
}
