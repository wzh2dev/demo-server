package com.demo.server.inventory.dto;

/**
 * 库存调整请求。
 */
public class InventoryAdjustRequest {
    private int delta;
    private String reason;

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
