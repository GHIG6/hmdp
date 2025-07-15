package com.hmall.coupon.constants;

/**
 * Redis常量
 */
public class RedisConstants {
    
    /**
     * 优惠券库存Key前缀
     */
    public static final String COUPON_STOCK_KEY = "coupon:stock:";
    
    /**
     * 优惠券信息Key前缀
     */
    public static final String COUPON_INFO_KEY = "coupon:info:";
    
    /**
     * 用户领券限制Key前缀
     */
    public static final String COUPON_LIMIT_KEY = "coupon:limit:";
    
    /**
     * 抢券记录Key前缀
     */
    public static final String COUPON_RECORD_KEY = "coupon:record:";
    
    /**
     * 待落库队列Key
     */
    public static final String COUPON_PENDING_KEY = "coupon:pending";
    
    /**
     * 优惠券信息缓存过期时间（秒）
     */
    public static final long COUPON_INFO_EXPIRE = 3600 * 24;
    
    /**
     * 抢券记录过期时间（秒）
     */
    public static final long COUPON_RECORD_EXPIRE = 3600 * 24;
}

