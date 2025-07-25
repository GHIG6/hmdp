package com.hmall.behavior.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hmall.behavior.enums.BehaviorType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户行为实体
 *
 * @author binZhang
 */
@Data
@TableName(value = "user_behavior", autoResultMap = true)
public class UserBehavior {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 行为类型：VIEW-浏览，FAVORITE-收藏，LIKE-点赞，SHARE-分享，CART-加购
     */
    private BehaviorType behaviorType;

    /**
     * 行为发生时间
     */
    private LocalDateTime createTime;

    /**
     * 扩展信息（JSON格式）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extendInfo;
}

