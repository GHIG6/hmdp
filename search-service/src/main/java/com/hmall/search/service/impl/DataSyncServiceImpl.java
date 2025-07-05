package com.hmall.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hmall.api.client.ItemClient;
import com.hmall.api.dto.ItemDTO;
import com.hmall.common.domain.PageDTO;
import com.hmall.common.domain.PageQuery;
import com.hmall.search.domain.ItemDoc;
import com.hmall.search.service.IDataSyncService;
import com.hmall.search.service.ISearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据同步服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncServiceImpl implements IDataSyncService {
    
    private final ISearchService searchService;
    private final ItemClient itemClient;
    
    @Override
    public void fullSync() throws IOException {
        log.info("开始全量同步商品数据到ES");
        
        int pageNo = 1;
        int pageSize = 100;
        int totalSynced = 0;
        
        while (true) {
            // 1.分页查询商品
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNo(pageNo);
            pageQuery.setPageSize(pageSize);
            
            PageDTO<ItemDTO> page = itemClient.queryItemByPage(pageQuery);
            List<ItemDTO> items = page.getList();
            
            if (items == null || items.isEmpty()) {
                break;
            }
            
            // 2.转换为ES文档
            List<ItemDoc> itemDocs = new ArrayList<>();
            for (ItemDTO item : items) {
                ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);
                // TODO: 这里可以补充店铺名称等扩展信息
                itemDoc.setShopName("黑马商城旗舰店");  // 示例，实际应该查询店铺信息
                itemDocs.add(itemDoc);
            }
            
            // 3.批量导入ES
            searchService.bulkImport(itemDocs);
            totalSynced += items.size();
            
            log.info("已同步第{}页，本批{}条，累计{}条", pageNo, items.size(), totalSynced);
            
            // 4.判断是否还有下一页
            if (items.size() < pageSize) {
                break;
            }
            pageNo++;
        }
        
        log.info("全量同步完成，共同步{}条商品数据", totalSynced);
    }
}

