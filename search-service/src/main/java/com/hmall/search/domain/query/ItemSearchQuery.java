package com.hmall.search.domain.query;

import com.hmall.common.domain.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品搜索查询条件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "商品搜索查询条件")
public class ItemSearchQuery extends PageQuery {
    
    @ApiModelProperty("搜索关键字 - 支持商品名称、店铺名称搜索")
    private String key;
    
    @ApiModelProperty("商品分类 - 精确匹配")
    private String category;
    
    @ApiModelProperty("商品品牌 - 精确匹配")
    private String brand;
    
    @ApiModelProperty("价格最小值")
    private Integer minPrice;
    
    @ApiModelProperty("价格最大值")
    private Integer maxPrice;
    
    @ApiModelProperty("排序字段: price-价格, sold-销量, commentCount-评论数")
    private String sortBy;
    
    @ApiModelProperty("是否降序: true-降序, false-升序")
    private Boolean isAsc = false;
}

