package com.hmall.search.service;

import com.hmall.common.domain.PageDTO;
import com.hmall.search.domain.ItemDoc;
import com.hmall.search.domain.query.ItemSearchQuery;
import com.hmall.search.domain.vo.ItemVO;

import java.io.IOException;
import java.util.List;

/**
 * 商品搜索服务接口
 */
public interface ISearchService {
    
    /**
     * 搜索商品
     * @param query 搜索条件
     * @return 分页结果
     */
    PageDTO<ItemVO> search(ItemSearchQuery query) throws IOException;
    
    /**
     * 创建索引
     * @throws IOException IO异常
     */
    void createIndex() throws IOException;
    
    /**
     * 删除索引
     * @throws IOException IO异常
     */
    void deleteIndex() throws IOException;
    
    /**
     * 批量导入商品数据到ES
     * @param items 商品列表
     * @throws IOException IO异常
     */
    void bulkImport(List<ItemDoc> items) throws IOException;
    
    /**
     * 添加或更新单个商品
     * @param item 商品文档
     * @throws IOException IO异常
     */
    void saveOrUpdate(ItemDoc item) throws IOException;
    
    /**
     * 删除商品
     * @param id 商品ID
     * @throws IOException IO异常
     */
    void deleteById(Long id) throws IOException;
}

