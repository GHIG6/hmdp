package com.hmall.behavior.domain.dto;

import com.hmall.behavior.enums.BehaviorType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户行为数据传输对象
 *
 * @author binZhang
 */
@Data
@ApiModel(description = "用户行为数据传输对象")
public class BehaviorDTO {
    
    @ApiModelProperty(value = "用户ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @ApiModelProperty(value = "行为类型：VIEW-浏览，FAVORITE-收藏，LIKE-点赞，SHARE-分享，CART-加购", required = true)
    private BehaviorType behaviorType;

    @ApiModelProperty(value = "行为发生时间", hidden = true)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "扩展信息（可选）")
    private Map<String, Object> extendInfo;
}

