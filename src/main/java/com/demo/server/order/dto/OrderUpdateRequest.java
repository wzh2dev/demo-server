package com.demo.server.order.dto;

import com.demo.server.order.domain.OrderStatus;

/**
 * 订单更新请求。
 */
public class OrderUpdateRequest {
    private OrderStatus status;
    private String remark;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
