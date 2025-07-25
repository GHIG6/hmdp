package com.hmall.behavior.example;

import com.hmall.api.client.BehaviorClient;
import com.hmall.api.dto.BehaviorDTO;
import com.hmall.api.vo.BehaviorStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Behavior Service 使用示例
 * 
 * 本类展示如何在其他微服务中调用 behavior-service
 * 
 * @author zhangbin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorUsageExample {

    private final BehaviorClient behaviorClient;

    /**
     * 示例1：在商品详情接口中记录浏览行为（推荐异步方式）
     * 
     * 适用场景：item-service 的商品详情接口
     */
    public void recordViewBehaviorAsync(Long itemId) {
        // 异步记录，不阻塞主业务流程
        CompletableFuture.runAsync(() -> {
            try {
                BehaviorDTO dto = new BehaviorDTO();
                dto.setItemId(itemId);
                dto.setBehaviorType(1); // 1-浏览
                behaviorClient.recordBehavior(dto);
                log.debug("记录浏览行为成功: itemId={}", itemId);
            } catch (Exception e) {
                // 记录失败不影响主业务
                log.warn("记录浏览行为失败: itemId={}, error={}", itemId, e.getMessage());
            }
        });
    }

    /**
     * 示例2：记录收藏行为（同步方式）
     * 
     * 适用场景：用户点击收藏按钮
     */
    public void recordFavoriteBehavior(Long itemId) {
        BehaviorDTO dto = new BehaviorDTO();
        dto.setItemId(itemId);
        dto.setBehaviorType(2); // 2-收藏
        behaviorClient.recordBehavior(dto);
    }

    /**
     * 示例3：记录点赞行为
     * 
     * 适用场景：用户点击点赞按钮
     */
    public void recordLikeBehavior(Long itemId) {
        BehaviorDTO dto = new BehaviorDTO();
        dto.setItemId(itemId);
        dto.setBehaviorType(3); // 3-点赞
        behaviorClient.recordBehavior(dto);
    }

    /**
     * 示例4：记录分享行为
     * 
     * 适用场景：用户点击分享按钮
     */
    public void recordShareBehavior(Long itemId) {
        BehaviorDTO dto = new BehaviorDTO();
        dto.setItemId(itemId);
        dto.setBehaviorType(4); // 4-分享
        behaviorClient.recordBehavior(dto);
    }

    /**
     * 示例5：记录加购行为
     * 
     * 适用场景：cart-service 的添加购物车接口
     */
    public void recordCartBehavior(Long itemId) {
        BehaviorDTO dto = new BehaviorDTO();
        dto.setItemId(itemId);
        dto.setBehaviorType(5); // 5-加购
        behaviorClient.recordBehavior(dto);
    }

    /**
     * 示例6：查询商品行为统计
     * 
     * 适用场景：商品详情页展示统计数据
     */
    public BehaviorStatsVO getItemStats(Long itemId) {
        return behaviorClient.getItemStats(itemId);
    }

    /**
     * 示例7：检查用户是否收藏了某商品
     * 
     * 适用场景：商品详情页判断是否已收藏
     */
    public Boolean checkIfFavorited(Long itemId) {
        return behaviorClient.checkBehavior(itemId, 2); // 2-收藏
    }

    /**
     * 示例8：检查用户是否点赞了某商品
     */
    public Boolean checkIfLiked(Long itemId) {
        return behaviorClient.checkBehavior(itemId, 3); // 3-点赞
    }
}

/**
 * ====================================
 * 在 item-service 中的完整使用示例
 * ====================================
 * 
 * ItemController.java:
 * 
 * @RestController
 * @RequestMapping("/items")
 * @RequiredArgsConstructor
 * public class ItemController {
 *     
 *     private final IItemService itemService;
 *     private final BehaviorClient behaviorClient;
 *     
 *     // 查询商品详情（记录浏览行为）
 *     @GetMapping("/{id}")
 *     public ItemVO getItemById(@PathVariable Long id) {
 *         // 1. 查询商品信息
 *         ItemVO item = itemService.getById(id);
 *         
 *         // 2. 异步记录浏览行为
 *         CompletableFuture.runAsync(() -> {
 *             BehaviorDTO dto = new BehaviorDTO();
 *             dto.setItemId(id);
 *             dto.setBehaviorType(1); // 1-浏览
 *             behaviorClient.recordBehavior(dto);
 *         });
 *         
 *         // 3. 查询并填充统计数据
 *         BehaviorStatsVO stats = behaviorClient.getItemStats(id);
 *         item.setViewCount(stats.getViewCount());
 *         item.setLikeCount(stats.getLikeCount());
 *         
 *         // 4. 检查当前用户是否收藏
 *         item.setIsFavorited(behaviorClient.checkBehavior(id, 2));
 *         
 *         return item;
 *     }
 *     
 *     // 收藏商品
 *     @PostMapping("/{id}/favorite")
 *     public String favoriteItem(@PathVariable Long id) {
 *         BehaviorDTO dto = new BehaviorDTO();
 *         dto.setItemId(id);
 *         dto.setBehaviorType(2); // 2-收藏
 *         return behaviorClient.recordBehavior(dto);
 *     }
 *     
 *     // 点赞商品
 *     @PostMapping("/{id}/like")
 *     public String likeItem(@PathVariable Long id) {
 *         BehaviorDTO dto = new BehaviorDTO();
 *         dto.setItemId(id);
 *         dto.setBehaviorType(3); // 3-点赞
 *         return behaviorClient.recordBehavior(dto);
 *     }
 * }
 * 
 * ====================================
 * 在 cart-service 中的使用示例
 * ====================================
 * 
 * CartServiceImpl.java:
 * 
 * @Service
 * @RequiredArgsConstructor
 * public class CartServiceImpl implements ICartService {
 *     
 *     private final BehaviorClient behaviorClient;
 *     
 *     @Override
 *     public void addItem2Cart(CartFormDTO cartFormDTO) {
 *         // ... 添加购物车逻辑 ...
 *         
 *         // 记录加购行为
 *         CompletableFuture.runAsync(() -> {
 *             BehaviorDTO dto = new BehaviorDTO();
 *             dto.setItemId(cartFormDTO.getItemId());
 *             dto.setBehaviorType(5); // 5-加购
 *             behaviorClient.recordBehavior(dto);
 *         });
 *     }
 * }
 * 
 * ====================================
 * 在 hm-gateway 中添加路由配置
 * ====================================
 * 
 * application.yaml:
 * 
 * spring:
 *   cloud:
 *     gateway:
 *       routes:
 *         - id: behavior-service
 *           uri: lb://behavior-service
 *           predicates:
 *             - Path=/behaviors/**
 * 
 */

