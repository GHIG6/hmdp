package com.hmall.behavior.domain.vo;

import com.hmall.behavior.enums.BehaviorType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户足迹展示对象
 *
 * @author binZhang
 */
@Data
@ApiModel(description = "用户足迹展示对象")
public class FootprintVO {

    @ApiModelProperty("行为ID")
    private Long id;

    @ApiModelProperty("商品ID")
    private Long itemId;

    @ApiModelProperty("商品名称")
    private String itemName;

    @ApiModelProperty("商品图片")
    private String itemImage;

    @ApiModelProperty("商品价格")
    private Integer price;

    @ApiModelProperty("行为类型")
    private BehaviorType behaviorType;

    @ApiModelProperty("行为发生时间")
    private LocalDateTime createTime;
}

