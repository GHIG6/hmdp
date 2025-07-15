package com.hmall.coupon.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 优惠券DTO
 */
@Data
public class CouponDTO {
    /**
     * 优惠券名称
     */
    private String name;
    
    /**
     * 类型：1-满减券 2-折扣券 3-随机金额券
     */
    private Integer type;
    
    /**
     * 优惠金额（分）
     */
    private Integer discountAmount;
    
    /**
     * 折扣率（例如：85表示8.5折）
     */
    private Integer discountRate;
    
    /**
     * 最低消费金额（分）
     */
    private Integer minAmount;
    
    /**
     * 总库存
     */
    private Integer totalStock;
    
    /**
     * 每人限领数量
     */
    private Integer perUserLimit;
    
    /**
     * 开始时间
     */
    private LocalDateTime beginTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 领取后有效天数
     */
    private Integer validDays;
}

