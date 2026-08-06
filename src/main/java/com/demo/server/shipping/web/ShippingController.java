package com.demo.server.shipping.web;

import com.demo.server.shipping.domain.Shipping;
import com.demo.server.shipping.domain.ShippingStatus;
import com.demo.server.shipping.domain.Track;
import com.demo.server.shipping.dto.ShippingCreateRequest;
import com.demo.server.shipping.dto.TrackCreateRequest;
import com.demo.server.shipping.exception.BizException;
import com.demo.server.shipping.service.ShippingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流控制器。
 */
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    public Response create(ShippingCreateRequest req) {
        return Response.created(shippingService.create(req));
    }

    public Response get(Long id) {
        return Response.ok(shippingService.get(id));
    }

    public Response getByOrder(Long orderId) {
        return Response.ok(shippingService.getByOrder(orderId));
    }

    public Response list(ShippingStatus status, int pageNumber, int pageSize) {
        List<Shipping> list = shippingService.list(status, pageNumber, pageSize);
        return Response.ok(list);
    }

    public Response appendTrack(Long shippingId, TrackCreateRequest req) {
        return Response.created(shippingService.appendTrack(shippingId, req));
    }

    public Response listTracks(Long shippingId) {
        List<Track> tracks = shippingService.listTracks(shippingId);
        return Response.ok(tracks);
    }

    public Response markDelivered(Long id) {
        return Response.ok(shippingService.markDelivered(id));
    }

    public Response markException(Long id, String reason) {
        return Response.ok(shippingService.markException(id, reason));
    }

    public Response delete(Long id) {
        shippingService.delete(id);
        return Response.noContent();
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
}
