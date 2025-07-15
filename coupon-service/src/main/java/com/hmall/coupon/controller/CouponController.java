package com.hmall.coupon.controller;

import com.hmall.common.utils.UserContext;
import com.hmall.coupon.domain.dto.CouponDTO;
import com.hmall.coupon.domain.vo.CouponVO;
import com.hmall.coupon.service.ICouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券管理接口
 */
@Api(tags = "优惠券管理接口")
@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {
    
    private final ICouponService couponService;
    
    /**
     * 创建优惠券
     */
    @ApiOperation("创建优惠券")
    @PostMapping
    public Long createCoupon(@RequestBody CouponDTO couponDTO) {
        return couponService.createCoupon(couponDTO);
    }
    
    /**
     * 获取优惠券列表
     */
    @ApiOperation("获取优惠券列表")
    @GetMapping("/list")
    public List<CouponVO> listCoupons() {
        return couponService.listCoupons();
    }
    
    /**
     * 获取优惠券详情
     */
    @ApiOperation("获取优惠券详情")
    @GetMapping("/{id}")
    public CouponVO getCouponById(@PathVariable("id") Long id) {
        return couponService.getCouponById(id);
    }
    
    /**
     * 手动预加载优惠券到Redis
     */
    @ApiOperation("预加载优惠券到Redis")
    @PostMapping("/{id}/preload")
    public String preloadToRedis(@PathVariable("id") Long id) {
        couponService.preloadToRedis(id);
        return "预加载成功";
    }
    
    /**
     * 预加载所有活动中的优惠券到Redis
     */
    @ApiOperation("预加载所有活动优惠券")
    @PostMapping("/preload/all")
    public String preloadAllActive() {
        couponService.preloadAllActiveToRedis();
        return "批量预加载成功";
    }
}

