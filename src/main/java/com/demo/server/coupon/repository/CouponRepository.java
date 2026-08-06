package com.demo.server.coupon.repository;

import com.demo.server.coupon.domain.Coupon;
import com.demo.server.coupon.domain.CouponStatus;
import com.demo.server.coupon.domain.UserCoupon;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 优惠券仓储，内存实现。
 */
public class CouponRepository {

    private final Map<Long, Coupon> store = new ConcurrentHashMap<>();
    private final Map<Long, UserCoupon> userCoupons = new ConcurrentHashMap<>();
    private final AtomicLong couponIdSeq = new AtomicLong(0L);
    private final AtomicLong userCouponIdSeq = new AtomicLong(0L);

    public Coupon save(Coupon coupon) {
        if (coupon.getId() == null) {
            coupon.setId(couponIdSeq.incrementAndGet());
            long now = System.currentTimeMillis();
            coupon.setCreatedAt(now);
            coupon.setUpdatedAt(now);
        } else {
            coupon.setUpdatedAt(System.currentTimeMillis());
        }
        store.put(coupon.getId(), coupon);
        return coupon;
    }

    public Coupon findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public List<Coupon> findAll(CouponStatus status, int pageNumber, int pageSize) {
        return store.values().stream()
                .filter(c -> status == null || status == c.getStatus())
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public long count(CouponStatus status) {
        return store.values().stream()
                .filter(c -> status == null || status == c.getStatus())
                .count();
    }

    public UserCoupon saveUserCoupon(UserCoupon userCoupon) {
        if (userCoupon.getId() == null) {
            userCoupon.setId(userCouponIdSeq.incrementAndGet());
            userCoupon.setClaimedAt(System.currentTimeMillis());
        }
        userCoupons.put(userCoupon.getId(), userCoupon);
        return userCoupon;
    }

    public UserCoupon findUserCouponById(Long id) {
        if (id == null) {
            return null;
        }
        return userCoupons.get(id);
    }

    public List<UserCoupon> findUserCouponsByUser(Long userId, CouponStatus status) {
        if (userId == null) {
            return java.util.Collections.emptyList();
        }
        return userCoupons.values().stream()
                .filter(uc -> userId.equals(uc.getUserId()))
                .filter(uc -> status == null || status == uc.getStatus())
                .collect(Collectors.toList());
    }

    /**
     * 查找用户是否已领取某券。
     */
    public UserCoupon findUserCoupon(Long userId, Long couponId) {
        if (userId == null || couponId == null) {
            return null;
        }
        return userCoupons.values().stream()
                .filter(uc -> userId.equals(uc.getUserId()))
                .filter(uc -> couponId.equals(uc.getCouponId()))
                .findFirst()
                .orElse(null);
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    /**
     * 找出已过期但状态仍是 CLAIMED 的用户券。
     */
    public List<UserCoupon> findExpiredUserCoupons(long now) {
        return userCoupons.values().stream()
                .filter(uc -> uc.getStatus() == CouponStatus.CLAIMED)
                .filter(uc -> uc.getExpiredAt() != null && uc.getExpiredAt() <= now)
                .collect(Collectors.toList());
    }
}
