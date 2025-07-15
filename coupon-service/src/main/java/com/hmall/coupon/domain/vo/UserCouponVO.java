package com.hmall.coupon.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户优惠券VO
 */
@Data
public class UserCouponVO {
    /**
     * 用户优惠券ID
     */
    private Long id;
    
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 优惠券名称
     */
    private String couponName;
    
    /**
     * 类型：1-满减券 2-折扣券 3-随机金额券
     */
    private Integer type;
    
    /**
     * 优惠金额（分）
     */
    private Integer discountAmount;
    
    /**
     * 折扣率
     */
    private Integer discountRate;
    
    /**
     * 最低消费金额（分）
     */
    private Integer minAmount;
    
    /**
     * 状态：1-未使用 2-已使用 3-已过期
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 领取时间
     */
    private LocalDateTime receiveTime;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 使用时间
     */
    private LocalDateTime useTime;
}

