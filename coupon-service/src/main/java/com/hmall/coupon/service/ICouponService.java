package com.hmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.coupon.domain.Coupon;
import com.hmall.coupon.domain.dto.CouponDTO;
import com.hmall.coupon.domain.vo.CouponVO;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface ICouponService extends IService<Coupon> {
    
    /**
     * 创建优惠券
     */
    Long createCoupon(CouponDTO couponDTO);
    
    /**
     * 获取优惠券列表
     */
    List<CouponVO> listCoupons();
    
    /**
     * 获取优惠券详情
     */
    CouponVO getCouponById(Long couponId);
    
    /**
     * 预加载优惠券到Redis
     */
    void preloadToRedis(Long couponId);
    
    /**
     * 预加载所有进行中的优惠券到Redis
     */
    void preloadAllActiveToRedis();
}

