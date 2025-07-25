package com.hmall.behavior.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品行为统计展示对象
 *
 * @author binZhang
 */
@Data
@ApiModel(description = "商品行为统计展示对象")
public class BehaviorStatsVO {

    @ApiModelProperty("商品ID")
    private Long itemId;

    @ApiModelProperty("浏览次数")
    private Integer viewCount;

    @ApiModelProperty("收藏次数")
    private Integer favoriteCount;

    @ApiModelProperty("点赞次数")
    private Integer likeCount;

    @ApiModelProperty("分享次数")
    private Integer shareCount;

    @ApiModelProperty("加购次数")
    private Integer cartCount;
}

