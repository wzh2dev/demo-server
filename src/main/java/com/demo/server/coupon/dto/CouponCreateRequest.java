package com.demo.server.coupon.dto;

import com.demo.server.coupon.domain.CouponType;

/**
 * 优惠券创建请求。
 */
public class CouponCreateRequest {
    private String name;
    private CouponType type;
    private double value;
    private Double minSpend;
    private long total;
    private Long startAt;
    private Long endAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CouponType getType() {
        return type;
    }

    public void setType(CouponType type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Double getMinSpend() {
        return minSpend;
    }

    public void setMinSpend(Double minSpend) {
        this.minSpend = minSpend;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Long getStartAt() {
        return startAt;
    }

    public void setStartAt(Long startAt) {
        this.startAt = startAt;
    }

    public Long getEndAt() {
        return endAt;
    }

    public void setEndAt(Long endAt) {
        this.endAt = endAt;
    }
}
