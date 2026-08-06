package com.demo.server.cart.dto;

/**
 * 购物车项添加请求。
 */
public class CartAddRequest {
    private Long productId;
    private int quantity;
    private boolean selected = true;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
