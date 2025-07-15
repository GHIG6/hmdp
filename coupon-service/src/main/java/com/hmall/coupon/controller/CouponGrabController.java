package com.hmall.coupon.controller;

import com.hmall.common.utils.UserContext;
import com.hmall.coupon.enums.CouponGrabResult;
import com.hmall.coupon.service.ICouponGrabService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 抢券接口（用户端）
 */
@Api(tags = "抢券接口")
@RestController
@RequestMapping("/coupon/grab")
@RequiredArgsConstructor
@Slf4j
public class CouponGrabController {
    
    private final ICouponGrabService couponGrabService;
    
    /**
     * 抢券
     */
    @ApiOperation("抢券")
    @PostMapping("/{couponId}")
    public Map<String, Object> grabCoupon(@PathVariable("couponId") Long couponId) {
        // 从ThreadLocal获取用户ID
        Long userId = UserContext.getUser();
        
        log.info("用户{}开始抢券，优惠券ID：{}", userId, couponId);
        
        // 执行抢券逻辑
        CouponGrabResult result = couponGrabService.grabCoupon(couponId, userId);
        
        // 构建返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("code", result.getCode());
        response.put("message", result.getMessage());
        
        log.info("用户{}抢券结果：{}", userId, result.getMessage());
        
        return response;
    }
}

