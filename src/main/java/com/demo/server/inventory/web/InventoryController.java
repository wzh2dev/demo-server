package com.demo.server.inventory.web;

import com.demo.server.inventory.domain.Inventory;
import com.demo.server.inventory.domain.InventoryLog;
import com.demo.server.inventory.dto.InventoryAdjustRequest;
import com.demo.server.inventory.dto.InventoryLockRequest;
import com.demo.server.inventory.exception.BizException;
import com.demo.server.inventory.service.InventoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存控制器。
 */
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public Response get(Long skuId) {
        return Response.ok(inventoryService.get(skuId));
    }

    public Response init(Long skuId, int available) {
        return Response.created(inventoryService.init(skuId, available));
    }

    public Response inbound(Long skuId, int quantity, String reason) {
        return Response.ok(inventoryService.inbound(skuId, quantity, reason));
    }

    public Response outbound(Long skuId, int quantity, String reason) {
        return Response.ok(inventoryService.outbound(skuId, quantity, reason));
    }

    public Response lock(InventoryLockRequest req) {
        return Response.ok(inventoryService.lock(req));
    }

    public Response unlock(Long skuId, int quantity, String refType, String refId) {
        return Response.ok(inventoryService.unlock(skuId, quantity, refType, refId));
    }

    public Response deductLocked(Long skuId, int quantity, String refType, String refId) {
        return Response.ok(inventoryService.deductLocked(skuId, quantity, refType, refId));
    }

    public Response adjust(Long skuId, InventoryAdjustRequest req) {
        return Response.ok(inventoryService.adjust(skuId, req));
    }

    public Response listLogs(Long skuId) {
        List<InventoryLog> logs = inventoryService.listLogs(skuId);
        return Response.ok(logs);
    }

    public Response listLowStock(int threshold) {
        List<Inventory> list = inventoryService.listLowStock(threshold);
        return Response.ok(list);
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
