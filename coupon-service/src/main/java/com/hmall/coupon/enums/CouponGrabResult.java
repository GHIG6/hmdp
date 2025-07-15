package com.hmall.coupon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抢券结果枚举
 */
@Getter
@AllArgsConstructor
public enum CouponGrabResult {
    SUCCESS(1, "抢券成功"),
    STOCK_NOT_ENOUGH(-1, "库存不足"),
    LIMIT_EXCEEDED(-2, "超过领取次数"),
    ALREADY_GRABBED(-3, "您已经领取过了"),
    SYSTEM_ERROR(-999, "系统繁忙，请稍后重试");
    
    private final Integer code;
    private final String message;
    
    public static CouponGrabResult fromCode(Long code) {
        if (code == null) {
            return SYSTEM_ERROR;
        }
        for (CouponGrabResult result : values()) {
            if (result.getCode().equals(code.intValue())) {
                return result;
            }
        }
        return SYSTEM_ERROR;
    }
    
    public boolean isSuccess() {
        return this == SUCCESS;
    }
}

