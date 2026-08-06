package com.demo.server.order.vo;

import com.demo.server.order.domain.Order;
import com.demo.server.order.domain.OrderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单 Entity ↔ VO 转换器。
 */
public final class OrderConverter {

    private OrderConverter() {
    }

    public static OrderVO toVO(Order order) {
        if (order == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setStatus(order.getStatus() == null ? null : order.getStatus().name());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setAddressId(order.getAddressId());
        vo.setItems(toItemVOList(order.getItems()));
        vo.setRemark(order.getRemark());
        vo.setPaidAt(order.getPaidAt());
        vo.setShippedAt(order.getShippedAt());
        vo.setCompletedAt(order.getCompletedAt());
        vo.setCreatedAt(order.getCreatedAt());
        vo.setUpdatedAt(order.getUpdatedAt());
        return vo;
    }

    public static List<OrderVO> toVOList(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }
        List<OrderVO> list = new ArrayList<>(orders.size());
        for (Order order : orders) {
            list.add(toVO(order));
        }
        return list;
    }

    public static OrderItemVO toItemVO(OrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemVO vo = new OrderItemVO();
        vo.setId(item.getId());
        vo.setOrderId(item.getOrderId());
        vo.setProductId(item.getProductId());
        vo.setProductName(item.getName());
        vo.setProductImage(item.getSkuCode());
        vo.setUnitPrice(item.getPrice());
        vo.setQuantity(item.getQuantity());
        vo.setSubtotal(item.getSubtotal());
        return vo;
    }

    public static List<OrderItemVO> toItemVOList(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }
        List<OrderItemVO> list = new ArrayList<>(items.size());
        for (OrderItem item : items) {
            list.add(toItemVO(item));
        }
        return list;
    }
}
