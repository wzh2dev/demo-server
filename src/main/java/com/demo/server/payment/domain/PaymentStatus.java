package com.demo.server.payment.domain;

/**
 * 支付状态。
 */
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED
}
