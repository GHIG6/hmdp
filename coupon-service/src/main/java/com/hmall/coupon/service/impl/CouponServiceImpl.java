package com.hmall.coupon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.coupon.constants.RedisConstants;
import com.hmall.coupon.domain.Coupon;
import com.hmall.coupon.domain.dto.CouponDTO;
import com.hmall.coupon.domain.vo.CouponVO;
import com.hmall.coupon.mapper.CouponMapper;
import com.hmall.coupon.service.ICouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {
    
    private final StringRedisTemplate redisTemplate;
    
    @Override
    public Long createCoupon(CouponDTO couponDTO) {
        // 1. DTO转实体
        Coupon coupon = BeanUtil.copyProperties(couponDTO, Coupon.class);
        coupon.setAvailableStock(couponDTO.getTotalStock());
        coupon.setStatus(2); // 进行中
        
        // 2. 保存到数据库
        save(coupon);
        
        // 3. 预加载到Redis
        preloadToRedis(coupon.getId());
        
        return coupon.getId();
    }
    
    @Override
    public List<CouponVO> listCoupons() {
        List<Coupon> coupons = list(new LambdaQueryWrapper<Coupon>()
                .orderByDesc(Coupon::getCreateTime));
        
        return coupons.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public CouponVO getCouponById(Long couponId) {
        Coupon coupon = getById(couponId);
        if (coupon == null) {
            return null;
        }
        return convertToVO(coupon);
    }
    
    @Override
    public void preloadToRedis(Long couponId) {
        // 1. 从数据库查询优惠券
        Coupon coupon = getById(couponId);
        if (coupon == null) {
            log.warn("优惠券{}不存在", couponId);
            return;
        }
        
        // 2. 加载库存到Redis
        String stockKey = RedisConstants.COUPON_STOCK_KEY + couponId;
        redisTemplate.opsForValue().set(stockKey, String.valueOf(coupon.getAvailableStock()));
        
        // 3. 加载优惠券信息到Redis（Hash）
        String infoKey = RedisConstants.COUPON_INFO_KEY + couponId;
        redisTemplate.opsForHash().put(infoKey, "id", String.valueOf(coupon.getId()));
        redisTemplate.opsForHash().put(infoKey, "name", coupon.getName());
        redisTemplate.opsForHash().put(infoKey, "type", String.valueOf(coupon.getType()));
        redisTemplate.opsForHash().put(infoKey, "perUserLimit", String.valueOf(coupon.getPerUserLimit()));
        redisTemplate.opsForHash().put(infoKey, "validDays", String.valueOf(coupon.getValidDays()));
        
        // 设置过期时间
        redisTemplate.expire(infoKey, RedisConstants.COUPON_INFO_EXPIRE, TimeUnit.SECONDS);
        
        log.info("优惠券{}预加载到Redis成功，库存：{}", couponId, coupon.getAvailableStock());
    }
    
    @Override
    public void preloadAllActiveToRedis() {
        // 查询所有进行中的优惠券
        List<Coupon> activeCoupons = list(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, 2)
                .le(Coupon::getBeginTime, LocalDateTime.now())
                .ge(Coupon::getEndTime, LocalDateTime.now()));
        
        log.info("开始预加载{}个活动中的优惠券到Redis", activeCoupons.size());
        
        for (Coupon coupon : activeCoupons) {
            preloadToRedis(coupon.getId());
        }
        
        log.info("优惠券预加载完成");
    }
    
    /**
     * 转换为VO
     */
    private CouponVO convertToVO(Coupon coupon) {
        CouponVO vo = BeanUtil.copyProperties(coupon, CouponVO.class);
        
        // 设置状态描述
        switch (coupon.getStatus()) {
            case 1:
                vo.setStatusDesc("未开始");
                break;
            case 2:
                vo.setStatusDesc("进行中");
                break;
            case 3:
                vo.setStatusDesc("已结束");
                break;
            case 4:
                vo.setStatusDesc("已暂停");
                break;
        }
        
        return vo;
    }
}

