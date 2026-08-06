package com.demo.server.coupon.web;

import com.demo.server.coupon.domain.Coupon;
import com.demo.server.coupon.domain.CouponStatus;
import com.demo.server.coupon.domain.UserCoupon;
import com.demo.server.coupon.dto.CouponCreateRequest;
import com.demo.server.coupon.dto.CouponUseRequest;
import com.demo.server.coupon.dto.PageResult;
import com.demo.server.coupon.exception.BizException;
import com.demo.server.coupon.service.CouponService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券控制器。
 */
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    public Response create(CouponCreateRequest req) {
        return Response.created(couponService.create(req));
    }

    public Response get(Long id) {
        return Response.ok(couponService.get(id));
    }

    public Response list(CouponStatus status, int pageNumber, int pageSize) {
        PageResult<Coupon> result = couponService.list(status, pageNumber, pageSize);
        return Response.ok(result);
    }

    public Response claim(Long userId, Long couponId) {
        return Response.created(couponService.claim(userId, couponId));
    }

    public Response use(CouponUseRequest req) {
        double discount = couponService.use(req);
        Map<String, Object> body = new HashMap<>();
        body.put("discount", discount);
        return Response.ok(body);
    }

    public Response release(Long userCouponId) {
        return Response.ok(couponService.release(userCouponId));
    }

    public Response listUserCoupons(Long userId, CouponStatus status) {
        List<UserCoupon> list = couponService.listUserCoupons(userId, status);
        return Response.ok(list);
    }

    public Response scanExpired() {
        int n = couponService.scanExpired();
        Map<String, Object> body = new HashMap<>();
        body.put("expired", n);
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
            case "CONFLICT":
                return 409;
            case "INVALID_ARGUMENT":
                return 400;
            default:
                return 500;
        }
    }
}
