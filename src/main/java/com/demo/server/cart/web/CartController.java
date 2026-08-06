package com.demo.server.cart.web;

import com.demo.server.cart.domain.CartItem;
import com.demo.server.cart.dto.CartAddRequest;
import com.demo.server.cart.dto.CartUpdateRequest;
import com.demo.server.cart.exception.BizException;
import com.demo.server.cart.service.CartService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器。
 */
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    public Response add(Long userId, CartAddRequest req) {
        return Response.created(cartService.add(userId, req));
    }

    public Response update(Long userId, Long itemId, CartUpdateRequest req) {
        return Response.ok(cartService.update(userId, itemId, req));
    }

    public Response remove(Long userId, Long itemId) {
        cartService.remove(userId, itemId);
        return Response.noContent();
    }

    public Response clear(Long userId) {
        cartService.clear(userId);
        return Response.noContent();
    }

    public Response clearSelected(Long userId) {
        int n = cartService.clearSelected(userId);
        Map<String, Object> body = new HashMap<>();
        body.put("removed", n);
        return Response.ok(body);
    }

    public Response list(Long userId, Boolean selected) {
        List<CartItem> items = cartService.list(userId, selected);
        return Response.ok(items);
    }

    public Response count(Long userId) {
        int n = cartService.count(userId);
        Map<String, Object> body = new HashMap<>();
        body.put("count", n);
        return Response.ok(body);
    }

    public Response setAllSelected(Long userId, boolean selected) {
        int n = cartService.setAllSelected(userId, selected);
        Map<String, Object> body = new HashMap<>();
        body.put("updated", n);
        return Response.ok(body);
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
            case "INVALID_ARGUMENT":
                return 400;
            default:
                return 500;
        }
    }
}
