package com.hmall.coupon.service;

import com.hmall.coupon.enums.CouponGrabResult;

/**
 * 抢券服务接口
 */
public interface ICouponGrabService {
    
    /**
     * 抢券
     */
    CouponGrabResult grabCoupon(Long couponId, Long userId);
}

