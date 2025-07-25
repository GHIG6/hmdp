package com.hmall.behavior.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户行为类型枚举
 *
 * @author binZhang
 */
@Getter
public enum BehaviorType {
    VIEW(1, "浏览"),
    FAVORITE(2, "收藏"),
    LIKE(3, "点赞"),
    SHARE(4, "分享"),
    CART(5, "加购");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    BehaviorType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}

