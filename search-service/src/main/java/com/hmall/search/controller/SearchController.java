package com.hmall.search.controller;

import com.hmall.common.domain.PageDTO;
import com.hmall.search.domain.query.ItemSearchQuery;
import com.hmall.search.domain.vo.ItemVO;
import com.hmall.search.service.ISearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 商品搜索控制器
 * 使用ElasticSearch实现高性能搜索
 */
@Api(tags = "商品搜索接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    
    private final ISearchService searchService;
    
    /**
     * 搜索商品
     * 支持：
     * 1. 商品名称搜索（中文分词）
     * 2. 店铺名称搜索（中文分词）
     * 3. 多字段联合搜索，商品名称权重更高
     * 4. 分类、品牌精确过滤
     * 5. 价格区间过滤
     * 6. 多种排序方式（相关度、价格、销量、评论数）
     */
    @ApiOperation("搜索商品")
    @GetMapping("/list")
    public PageDTO<ItemVO> search(ItemSearchQuery query) throws IOException {
        return searchService.search(query);
    }
    
    /**
     * 创建ES索引
     * 仅用于首次部署或索引重建
     */
    @ApiOperation("创建索引")
    @PostMapping("/index")
    public String createIndex() throws IOException {
        searchService.createIndex();
        return "索引创建成功";
    }
    
    /**
     * 删除ES索引
     * 谨慎操作
     */
    @ApiOperation("删除索引")
    @DeleteMapping("/index")
    public String deleteIndex() throws IOException {
        searchService.deleteIndex();
        return "索引删除成功";
    }
}

