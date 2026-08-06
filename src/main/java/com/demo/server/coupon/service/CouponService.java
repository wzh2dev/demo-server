package com.demo.server.coupon.service;

import com.demo.server.coupon.domain.Coupon;
import com.demo.server.coupon.domain.CouponStatus;
import com.demo.server.coupon.domain.CouponType;
import com.demo.server.coupon.domain.UserCoupon;
import com.demo.server.coupon.dto.CouponCreateRequest;
import com.demo.server.coupon.dto.CouponUseRequest;
import com.demo.server.coupon.dto.PageResult;
import com.demo.server.coupon.exception.ConflictException;
import com.demo.server.coupon.exception.NotFoundException;
import com.demo.server.coupon.exception.ValidationException;
import com.demo.server.coupon.repository.CouponRepository;

import java.util.List;
import java.util.Objects;

/**
 * 优惠券领域服务。
 */
public class CouponService {

    private static final int NAME_MAX_LEN = 64;
    private static final double AMOUNT_EPSILON = 0.0001d;

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = Objects.requireNonNull(couponRepository);
    }

    /**
     * 创建优惠券。
     */
    public Coupon create(CouponCreateRequest req) {
        validateCreate(req);
        Coupon coupon = new Coupon();
        coupon.setName(req.getName());
        coupon.setType(req.getType());
        coupon.setValue(req.getValue());
        coupon.setMinSpend(req.getMinSpend() == null ? 0d : req.getMinSpend());
        coupon.setTotal(req.getTotal());
        coupon.setClaimed(0L);
        coupon.setStatus(CouponStatus.UNCLAIMED);
        coupon.setStartAt(req.getStartAt());
        coupon.setEndAt(req.getEndAt());
        return couponRepository.save(coupon);
    }

    public Coupon get(Long id) {
        return requireCoupon(id);
    }

    public PageResult<Coupon> list(CouponStatus status, int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 20;
        }
        List<Coupon> items = couponRepository.findAll(status, pageNumber, pageSize);
        long total = couponRepository.count(status);
        return new PageResult<>(items, total, pageNumber, pageSize);
    }

    /**
     * 用户领取优惠券。
     */
    public UserCoupon claim(Long userId, Long couponId) {
        if (userId == null) {
            throw new ValidationException("userId is null");
        }
        Coupon coupon = requireCoupon(couponId);
        long now = System.currentTimeMillis();
        if (coupon.getStartAt() != null && now < coupon.getStartAt()) {
            throw new ConflictException("coupon not started");
        }
        if (coupon.getEndAt() != null && now > coupon.getEndAt()) {
            throw new ConflictException("coupon already ended");
        }
        if (coupon.getRemaining() <= 0) {
            throw new ConflictException("coupon sold out");
        }
        if (couponRepository.findUserCoupon(userId, couponId) != null) {
            throw new ConflictException("user already claimed this coupon");
        }
        coupon.setClaimed(coupon.getClaimed() + 1);
        if (coupon.getRemaining() <= 0) {
            coupon.setStatus(CouponStatus.CLAIMED);
        }
        couponRepository.save(coupon);

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(CouponStatus.CLAIMED);
        userCoupon.setExpiredAt(coupon.getEndAt());
        return couponRepository.saveUserCoupon(userCoupon);
    }

    /**
     * 核销优惠券，返回抵扣金额。
     */
    public double use(CouponUseRequest req) {
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getOrderAmount() < 0) {
            throw new ValidationException("orderAmount must be >= 0");
        }
        UserCoupon userCoupon = couponRepository.findUserCouponById(req.getUserCouponId());
        if (userCoupon == null) {
            throw new NotFoundException("userCoupon", req.getUserCouponId());
        }
        if (userCoupon.getStatus() != CouponStatus.CLAIMED) {
            throw new ConflictException("userCoupon not claimable: " + userCoupon.getStatus());
        }
        long now = System.currentTimeMillis();
        if (userCoupon.getExpiredAt() != null && userCoupon.getExpiredAt() < now) {
            userCoupon.setStatus(CouponStatus.EXPIRED);
            couponRepository.saveUserCoupon(userCoupon);
            throw new ConflictException("userCoupon expired");
        }
        Coupon coupon = requireCoupon(userCoupon.getCouponId());
        if (req.getOrderAmount() < coupon.getMinSpend() - AMOUNT_EPSILON) {
            throw new ValidationException("orderAmount less than minSpend");
        }
        double discount = computeDiscount(coupon, req.getOrderAmount());
        if (discount > req.getOrderAmount()) {
            discount = req.getOrderAmount();
        }
        userCoupon.setStatus(CouponStatus.USED);
        userCoupon.setUsedAt(System.currentTimeMillis());
        couponRepository.saveUserCoupon(userCoupon);
        return discount;
    }

    /**
     * 退还优惠券（订单取消时）。
     */
    public UserCoupon release(Long userCouponId) {
        UserCoupon userCoupon = couponRepository.findUserCouponById(userCouponId);
        if (userCoupon == null) {
            throw new NotFoundException("userCoupon", userCouponId);
        }
        if (userCoupon.getStatus() != CouponStatus.USED) {
            throw new ConflictException("userCoupon not used: " + userCoupon.getStatus());
        }
        long now = System.currentTimeMillis();
        if (userCoupon.getExpiredAt() != null && userCoupon.getExpiredAt() < now) {
            userCoupon.setStatus(CouponStatus.EXPIRED);
        } else {
            userCoupon.setStatus(CouponStatus.CLAIMED);
        }
        userCoupon.setUsedAt(null);
        return couponRepository.saveUserCoupon(userCoupon);
    }

    public List<UserCoupon> listUserCoupons(Long userId, CouponStatus status) {
        return couponRepository.findUserCouponsByUser(userId, status);
    }

    /**
     * 扫描并标记过期用户券。
     */
    public int scanExpired() {
        long now = System.currentTimeMillis();
        List<UserCoupon> expired = couponRepository.findExpiredUserCoupons(now);
        for (UserCoupon uc : expired) {
            uc.setStatus(CouponStatus.EXPIRED);
            couponRepository.saveUserCoupon(uc);
        }
        return expired.size();
    }

    private Coupon requireCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id);
        if (coupon == null) {
            throw new NotFoundException("coupon", id);
        }
        return coupon;
    }

    private double computeDiscount(Coupon coupon, double orderAmount) {
        if (coupon.getType() == CouponType.AMOUNT) {
            return coupon.getValue();
        }
        // PERCENTAGE
        return orderAmount * coupon.getValue() / 100d;
    }

    private void validateCreate(CouponCreateRequest req) {
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getName() == null || req.getName().isEmpty()) {
            throw new ValidationException("name is empty");
        }
        if (req.getName().length() > NAME_MAX_LEN) {
            throw new ValidationException("name too long");
        }
        if (req.getType() == null) {
            throw new ValidationException("type is null");
        }
        if (req.getValue() < 0) {
            throw new ValidationException("value must be >= 0");
        }
        if (req.getType() == CouponType.PERCENTAGE && req.getValue() > 100) {
            throw new ValidationException("percentage value must be <= 100");
        }
        if (req.getMinSpend() != null && req.getMinSpend() < 0) {
            throw new ValidationException("minSpend must be >= 0");
        }
        if (req.getTotal() < 0) {
            throw new ValidationException("total must be >= 0");
        }
        if (req.getStartAt() != null && req.getEndAt() != null
                && req.getStartAt() > req.getEndAt()) {
            throw new ValidationException("startAt > endAt");
        }
    }
}
