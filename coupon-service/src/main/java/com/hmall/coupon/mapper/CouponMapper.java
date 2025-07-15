package com.hmall.coupon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmall.coupon.domain.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 优惠券Mapper
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
    
    /**
     * 扣减库存（乐观锁）
     */
    @Update("UPDATE coupon SET available_stock = available_stock - 1 WHERE id = #{couponId} AND available_stock > 0")
    int deductStock(@Param("couponId") Long couponId);
}

