package com.hmall.behavior.domain.dto;

import com.hmall.behavior.enums.BehaviorType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户足迹查询对象
 *
 * @author binZhang
 */
@Data
@ApiModel(description = "用户足迹查询对象")
public class FootprintQueryDTO {

    @ApiModelProperty(value = "行为类型，不传则查询所有类型")
    private BehaviorType behaviorType;

    @ApiModelProperty(value = "页码，默认1")
    private Integer page = 1;

    @ApiModelProperty(value = "每页大小，默认20")
    private Integer size = 20;
}

