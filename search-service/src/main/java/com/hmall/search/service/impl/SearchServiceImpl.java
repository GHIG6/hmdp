package com.hmall.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmall.common.domain.PageDTO;
import com.hmall.search.domain.ItemDoc;
import com.hmall.search.domain.query.ItemSearchQuery;
import com.hmall.search.domain.vo.ItemVO;
import com.hmall.search.service.ISearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品搜索服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements ISearchService {
    
    private final RestHighLevelClient client;
    
    private static final String INDEX_NAME = "items";
    
    // ES索引映射定义 - 使用IK分词器
    private static final String INDEX_MAPPING = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"price\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"stock\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"image\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"category\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"brand\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"sold\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"commentCount\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"isAD\": {\n" +
            "        \"type\": \"boolean\"\n" +
            "      },\n" +
            "      \"shopName\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"all\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
    
    @Override
    public PageDTO<ItemVO> search(ItemSearchQuery query) throws IOException {
        // 1.构建搜索请求
        SearchRequest request = new SearchRequest(INDEX_NAME);
        
        // 2.构建查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 2.1.关键字搜索 - 使用multi_match实现多字段搜索（商品名称、店铺名称）
        if (StrUtil.isNotBlank(query.getKey())) {
            boolQuery.must(QueryBuilders.multiMatchQuery(query.getKey(), "name", "shopName", "all")
                    .field("name", 2.0f)  // 商品名称权重更高
                    .field("shopName", 1.5f));  // 店铺名称次之
        }
        
        // 2.2.分类过滤
        if (StrUtil.isNotBlank(query.getCategory())) {
            boolQuery.filter(QueryBuilders.termQuery("category", query.getCategory()));
        }
        
        // 2.3.品牌过滤
        if (StrUtil.isNotBlank(query.getBrand())) {
            boolQuery.filter(QueryBuilders.termQuery("brand", query.getBrand()));
        }
        
        // 2.4.价格范围过滤
        if (query.getMinPrice() != null || query.getMaxPrice() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price")
                    .gte(query.getMinPrice() != null ? query.getMinPrice() : 0)
                    .lte(query.getMaxPrice() != null ? query.getMaxPrice() : Integer.MAX_VALUE));
        }
        
        sourceBuilder.query(boolQuery);
        
        // 3.分页
        int page = query.getPageNo() == null ? 1 : query.getPageNo();
        int size = query.getPageSize() == null ? 10 : query.getPageSize();
        sourceBuilder.from((page - 1) * size).size(size);
        
        // 4.排序
        if (StrUtil.isNotBlank(query.getSortBy())) {
            SortOrder order = query.getIsAsc() ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(query.getSortBy(), order);
        } else {
            // 默认按相关度排序，如果没有关键词则按销量排序
            if (StrUtil.isBlank(query.getKey())) {
                sourceBuilder.sort("sold", SortOrder.DESC);
            }
        }
        
        request.source(sourceBuilder);
        
        // 5.执行搜索
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        
        // 6.解析结果
        SearchHit[] hits = response.getHits().getHits();
        List<ItemVO> items = new ArrayList<>();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            ItemDoc itemDoc = JSONUtil.toBean(json, ItemDoc.class);
            ItemVO itemVO = BeanUtil.copyProperties(itemDoc, ItemVO.class);
            items.add(itemVO);
        }
        
        // 7.返回分页结果
        long total = response.getHits().getTotalHits().value;
        return new PageDTO<>(total, (long) items.size(), items);
    }
    
    @Override
    public void createIndex() throws IOException {
        // 判断索引是否存在
        GetIndexRequest getRequest = new GetIndexRequest(INDEX_NAME);
        boolean exists = client.indices().exists(getRequest, RequestOptions.DEFAULT);
        if (exists) {
            log.info("索引{}已存在", INDEX_NAME);
            return;
        }
        
        // 创建索引
        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
        request.source(INDEX_MAPPING, XContentType.JSON);
        client.indices().create(request, RequestOptions.DEFAULT);
        log.info("索引{}创建成功", INDEX_NAME);
    }
    
    @Override
    public void deleteIndex() throws IOException {
        // 判断索引是否存在
        GetIndexRequest getRequest = new GetIndexRequest(INDEX_NAME);
        boolean exists = client.indices().exists(getRequest, RequestOptions.DEFAULT);
        if (!exists) {
            log.info("索引{}不存在", INDEX_NAME);
            return;
        }
        
        // 删除索引
        DeleteIndexRequest request = new DeleteIndexRequest(INDEX_NAME);
        client.indices().delete(request, RequestOptions.DEFAULT);
        log.info("索引{}删除成功", INDEX_NAME);
    }
    
    @Override
    public void bulkImport(List<ItemDoc> items) throws IOException {
        if (items == null || items.isEmpty()) {
            return;
        }
        
        BulkRequest bulkRequest = new BulkRequest();
        for (ItemDoc item : items) {
            IndexRequest request = new IndexRequest(INDEX_NAME).id(item.getId().toString());
            request.source(JSONUtil.toJsonStr(item), XContentType.JSON);
            bulkRequest.add(request);
        }
        
        client.bulk(bulkRequest, RequestOptions.DEFAULT);
        log.info("批量导入{}条数据到ES成功", items.size());
    }
    
    @Override
    public void saveOrUpdate(ItemDoc item) throws IOException {
        IndexRequest request = new IndexRequest(INDEX_NAME).id(item.getId().toString());
        request.source(JSONUtil.toJsonStr(item), XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);
        log.info("保存商品{}到ES成功", item.getId());
    }
    
    @Override
    public void deleteById(Long id) throws IOException {
        DeleteRequest request = new DeleteRequest(INDEX_NAME, id.toString());
        client.delete(request, RequestOptions.DEFAULT);
        log.info("从ES删除商品{}成功", id);
    }
}

