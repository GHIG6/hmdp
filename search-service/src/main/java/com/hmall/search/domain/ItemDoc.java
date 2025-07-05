package com.hmall.search.domain;

import lombok.Data;

/**
 * ES文档实体 - 商品
 * 对应ES中的索引: items
 */
@Data
public class ItemDoc {
    /**
     * 商品id
     */
    private Long id;
    
    /**
     * 商品名称 - 支持分词搜索
     */
    private String name;
    
    /**
     * 价格（分）
     */
    private Integer price;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 商品图片
     */
    private String image;
    
    /**
     * 类目名称 - 支持精确匹配和分词搜索
     */
    private String category;
    
    /**
     * 品牌名称 - 支持精确匹配和分词搜索
     */
    private String brand;
    
    /**
     * 销量 - 用于排序
     */
    private Integer sold;
    
    /**
     * 评论数 - 用于排序
     */
    private Integer commentCount;
    
    /**
     * 是否是推广广告
     */
    private Boolean isAD;
    
    /**
     * 店铺名称 - 新增字段，支持分词搜索
     * 这是一个扩展字段，实现多字段搜索能力
     */
    private String shopName;
}

