package com.demo.server.payment.web;

import com.demo.server.payment.domain.Payment;
import com.demo.server.payment.domain.PaymentChannel;
import com.demo.server.payment.domain.PaymentStatus;
import com.demo.server.payment.domain.Refund;
import com.demo.server.payment.dto.PaymentCreateRequest;
import com.demo.server.payment.dto.RefundRequest;
import com.demo.server.payment.exception.BizException;
import com.demo.server.payment.service.PaymentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付控制器。
 */
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public Response create(PaymentCreateRequest req) {
        return Response.created(paymentService.create(req));
    }

    public Response get(Long id) {
        return Response.ok(paymentService.get(id));
    }

    public Response getByOrder(Long orderId) {
        return Response.ok(paymentService.getByOrder(orderId));
    }

    public Response markSuccess(Long id, String tradeNo) {
        return Response.ok(paymentService.markSuccess(id, tradeNo));
    }

    public Response markFailed(Long id, String reason) {
        return Response.ok(paymentService.markFailed(id, reason));
    }

    public Response closeExpired() {
        int n = paymentService.closeExpired();
        Map<String, Object> body = new HashMap<>();
        body.put("closed", n);
        return Response.ok(body);
    }

    public Response refund(Long paymentId, RefundRequest req) {
        return Response.created(paymentService.refund(paymentId, req));
    }

    public Response listRefunds(Long paymentId) {
        List<Refund> refunds = paymentService.listRefunds(paymentId);
        return Response.ok(refunds);
    }

    public Response markRefundSuccess(Long refundId) {
        return Response.ok(paymentService.markRefundSuccess(refundId));
    }

    public Response markRefundFailed(Long refundId, String reason) {
        return Response.ok(paymentService.markRefundFailed(refundId, reason));
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

    @SuppressWarnings("unused")
    private PaymentChannel parseChannel(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return PaymentChannel.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private PaymentStatus parseStatus(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return PaymentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
