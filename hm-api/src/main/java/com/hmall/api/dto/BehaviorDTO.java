package com.hmall.api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户行为数据传输对象（API层）
 *
 * @author binZhang
 */
@Data
public class BehaviorDTO {
    
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 行为类型：1-浏览，2-收藏，3-点赞，4-分享，5-加购
     */
    private Integer behaviorType;

    /**
     * 行为发生时间
     */
    private LocalDateTime createTime;

    /**
     * 扩展信息（可选）
     */
    private Map<String, Object> extendInfo;
}

