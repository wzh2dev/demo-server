package com.demo.server.coupon.domain;

/**
 * 用户已领取优惠券。
 */
public class UserCoupon {
    private Long id;
    private Long userId;
    private Long couponId;
    private CouponStatus status = CouponStatus.CLAIMED;
    private Long claimedAt;
    private Long usedAt;
    private Long expiredAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public void setStatus(CouponStatus status) {
        this.status = status;
    }

    public Long getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(Long claimedAt) {
        this.claimedAt = claimedAt;
    }

    public Long getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(Long usedAt) {
        this.usedAt = usedAt;
    }

    public Long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Long expiredAt) {
        this.expiredAt = expiredAt;
    }
}
