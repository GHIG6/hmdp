package com.hmall.search.listener;

import com.hmall.search.service.ISearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 商品删除监听器
 * 监听商品的删除事件，从ES中删除
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "item-topic",
        consumerGroup = "search-item-delete-consumer-group",
        selectorExpression = "item-delete"  // 监听删除标签
)
public class ItemDeleteListener implements RocketMQListener<Long> {
    
    private final ISearchService searchService;
    
    /**
     * 监听商品删除事件
     */
    @Override
    public void onMessage(Long itemId) {
        try {
            log.info("监听到商品{}删除事件", itemId);
            searchService.deleteById(itemId);
            log.info("从ES删除商品{}成功", itemId);
        } catch (Exception e) {
            log.error("从ES删除商品{}失败", itemId, e);
            // RocketMQ 会自动重试
        }
    }
}

