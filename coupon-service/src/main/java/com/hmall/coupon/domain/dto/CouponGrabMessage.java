package com.hmall.coupon.domain.dto;

import lombok.Data;

/**
 * 抢券MQ消息
 */
@Data
public class CouponGrabMessage {
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 时间戳
     */
    private Long timestamp;
}

