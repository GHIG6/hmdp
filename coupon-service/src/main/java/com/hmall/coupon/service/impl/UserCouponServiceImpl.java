package com.hmall.coupon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.coupon.domain.Coupon;
import com.hmall.coupon.domain.UserCoupon;
import com.hmall.coupon.domain.vo.UserCouponVO;
import com.hmall.coupon.mapper.UserCouponMapper;
import com.hmall.coupon.service.ICouponService;
import com.hmall.coupon.service.IUserCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户优惠券服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements IUserCouponService {
    
    private final ICouponService couponService;
    
    @Override
    public List<UserCouponVO> listUserCoupons(Long userId) {
        // 1. 查询用户的优惠券
        List<UserCoupon> userCoupons = list(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .orderByDesc(UserCoupon::getReceiveTime));
        
        // 2. 转换为VO
        return userCoupons.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean saveGrabRecord(Long couponId, Long userId, Long timestamp) {
        try {
            // 1. 查询优惠券信息
            Coupon coupon = couponService.getById(couponId);
            if (coupon == null) {
                log.warn("优惠券{}不存在", couponId);
                return false;
            }
            
            // 2. 构建用户优惠券记录
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setCouponId(couponId);
            userCoupon.setStatus(1); // 未使用
            
            // 领取时间
            LocalDateTime receiveTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), 
                    ZoneId.systemDefault()
            );
            userCoupon.setReceiveTime(receiveTime);
            
            // 过期时间（领取后N天）
            LocalDateTime expireTime = receiveTime.plusDays(coupon.getValidDays());
            userCoupon.setExpireTime(expireTime);
            
            // 3. 保存到数据库（数据库有唯一索引：uk_user_coupon(user_id, coupon_id)）
            save(userCoupon);
            
            log.info("落库成功: couponId={}, userId={}", couponId, userId);
            return true;
            
        } catch (DuplicateKeyException e) {
            // 唯一索引冲突，说明已存在（幂等）
            log.warn("记录已存在（幂等）: couponId={}, userId={}", couponId, userId);
            return true; // 已存在也算成功
        } catch (Exception e) {
            log.error("数据库插入失败: couponId={}, userId={}", couponId, userId, e);
            return false;
        }
    }
    
    /**
     * 转换为VO
     */
    private UserCouponVO convertToVO(UserCoupon userCoupon) {
        UserCouponVO vo = BeanUtil.copyProperties(userCoupon, UserCouponVO.class);
        
        // 查询优惠券信息
        Coupon coupon = couponService.getById(userCoupon.getCouponId());
        if (coupon != null) {
            vo.setCouponName(coupon.getName());
            vo.setType(coupon.getType());
            vo.setDiscountAmount(coupon.getDiscountAmount());
            vo.setDiscountRate(coupon.getDiscountRate());
            vo.setMinAmount(coupon.getMinAmount());
        }
        
        // 设置状态描述
        switch (userCoupon.getStatus()) {
            case 1:
                vo.setStatusDesc("未使用");
                break;
            case 2:
                vo.setStatusDesc("已使用");
                break;
            case 3:
                vo.setStatusDesc("已过期");
                break;
        }
        
        return vo;
    }
}

