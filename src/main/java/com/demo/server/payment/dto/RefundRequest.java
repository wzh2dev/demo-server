package com.demo.server.payment.dto;

/**
 * 退款请求。
 */
public class RefundRequest {
    private double amount;
    private String reason;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
