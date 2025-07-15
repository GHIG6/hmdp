package com.hmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.coupon.domain.UserCoupon;
import com.hmall.coupon.domain.vo.UserCouponVO;

import java.util.List;

/**
 * 用户优惠券服务接口
 */
public interface IUserCouponService extends IService<UserCoupon> {
    
    /**
     * 获取用户的优惠券列表
     */
    List<UserCouponVO> listUserCoupons(Long userId);
    
    /**
     * 保存抢券记录到数据库
     */
    boolean saveGrabRecord(Long couponId, Long userId, Long timestamp);
}

