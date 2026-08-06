package com.demo.server.coupon.dto;

/**
 * 优惠券核销请求。
 */
public class CouponUseRequest {
    private Long userCouponId;
    private double orderAmount;

    public Long getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(Long userCouponId) {
        this.userCouponId = userCouponId;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }
}
