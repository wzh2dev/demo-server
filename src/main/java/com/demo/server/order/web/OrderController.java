package com.demo.server.order.web;

import com.demo.server.order.domain.Order;
import com.demo.server.order.domain.OrderStatus;
import com.demo.server.order.dto.OrderCreateRequest;
import com.demo.server.order.dto.OrderUpdateRequest;
import com.demo.server.order.dto.PageResult;
import com.demo.server.order.exception.BizException;
import com.demo.server.order.service.OrderService;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单控制器。
 */
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public Response list(Long userId, Map<String, String> params) {
        int pageNumber = parseInt(params.get("pageNumber"), 1);
        int pageSize = parseInt(params.get("pageSize"), 20);
        OrderStatus status = parseStatus(params.get("status"));
        PageResult<Order> result = orderService.list(userId, status, pageNumber, pageSize);
        return Response.ok(result);
    }

    public Response get(Long id) {
        return Response.ok(orderService.get(id));
    }

    public Response create(Long userId, OrderCreateRequest req) {
        return Response.created(orderService.create(userId, req));
    }

    public Response update(Long id, OrderUpdateRequest req) {
        return Response.ok(orderService.update(id, req));
    }

    public Response cancel(Long id) {
        return Response.ok(orderService.cancel(id));
    }

    public Response handleError(BizException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", ex.getCode());
        body.put("message", ex.getMessage());
        int status = mapStatus(ex.getCode());
        return new Response(status, body);
    }

    private int mapStatus(String code) {
        switch (code) {
            case "NOT_FOUND":
                return 404;
            case "CONFLICT":
                return 409;
            case "INVALID_ARGUMENT":
                return 400;
            default:
                return 500;
        }
    }

    private int parseInt(String value, int defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private OrderStatus parseStatus(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return OrderStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
