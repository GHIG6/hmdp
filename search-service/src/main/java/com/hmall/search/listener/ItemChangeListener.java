package com.hmall.search.listener;

import cn.hutool.core.bean.BeanUtil;
import com.hmall.api.client.ItemClient;
import com.hmall.api.dto.ItemDTO;
import com.hmall.search.domain.ItemDoc;
import com.hmall.search.service.ISearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 商品变更监听器 - 新增/修改
 * 监听商品的新增、修改事件，实时同步到ES
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "item-topic",
        consumerGroup = "search-item-consumer-group",
        selectorExpression = "item-insert || item-update"  // 监听新增和修改标签
)
public class ItemChangeListener implements RocketMQListener<Long> {
    
    private final ISearchService searchService;
    private final ItemClient itemClient;
    
    /**
     * 监听商品新增和修改事件
     */
    @Override
    public void onMessage(Long itemId) {
        try {
            log.info("监听到商品{}新增或修改事件", itemId);
            // 1.查询商品信息
            ItemDTO itemDTO = itemClient.queryItemById(itemId);
            if (itemDTO == null) {
                log.warn("商品{}不存在", itemId);
                return;
            }
            
            // 2.转换为ES文档
            ItemDoc itemDoc = BeanUtil.copyProperties(itemDTO, ItemDoc.class);
            // TODO: 这里可以补充店铺名称等扩展信息
            itemDoc.setShopName("黑马商城旗舰店");  // 示例，实际应该查询店铺信息
            
            // 3.保存到ES
            searchService.saveOrUpdate(itemDoc);
            log.info("商品{}同步到ES成功", itemId);
        } catch (Exception e) {
            log.error("商品{}同步到ES失败", itemId, e);
            // RocketMQ 会自动重试
        }
    }
}
