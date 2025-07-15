package com.hmall.coupon.domain.dto;

import lombok.Data;

/**
 * Redis中的抢券记录
 */
@Data
public class CouponRecord {
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
    
    /**
     * 状态：0-待落库 1-已落库
     */
    private Integer status;
}

