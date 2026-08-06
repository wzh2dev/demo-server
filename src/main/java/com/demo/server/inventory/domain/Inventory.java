package com.demo.server.inventory.domain;

/**
 * 库存实体。
 */
public class Inventory {
    private Long skuId;
    private int available;
    private int locked;
    private Long updatedAt;

    public Inventory() {
    }

    public Inventory(Long skuId, int available, int locked) {
        this.skuId = skuId;
        this.available = available;
        this.locked = locked;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getTotal() {
        return available + locked;
    }
}
