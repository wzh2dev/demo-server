package com.demo.server.cart.dto;

/**
 * 购物车项更新请求。
 */
public class CartUpdateRequest {
    private int quantity;
    private Boolean selected;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
