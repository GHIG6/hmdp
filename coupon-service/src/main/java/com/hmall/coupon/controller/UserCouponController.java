package com.hmall.coupon.controller;

import com.hmall.common.utils.UserContext;
import com.hmall.coupon.domain.vo.UserCouponVO;
import com.hmall.coupon.service.IUserCouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户优惠券接口
 */
@Api(tags = "用户优惠券接口")
@RestController
@RequestMapping("/user-coupon")
@RequiredArgsConstructor
public class UserCouponController {
    
    private final IUserCouponService userCouponService;
    
    /**
     * 获取我的优惠券列表
     */
    @ApiOperation("获取我的优惠券")
    @GetMapping("/my")
    public List<UserCouponVO> getMyCoupons() {
        Long userId = UserContext.getUser();
        return userCouponService.listUserCoupons(userId);
    }
}

