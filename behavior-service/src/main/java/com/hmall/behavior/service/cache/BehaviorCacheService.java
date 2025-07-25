package com.hmall.behavior.service.cache;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmall.behavior.domain.dto.BehaviorDTO;
import com.hmall.behavior.domain.vo.BehaviorStatsVO;
import com.hmall.behavior.enums.BehaviorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 行为缓存服务（Redis）
 *
 * @author binZhang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BehaviorCacheService {

    private final StringRedisTemplate redisTemplate;

    private static final String VIEW_KEY_PREFIX = "behavior:view:user:";
    private static final String FAVORITE_KEY_PREFIX = "behavior:favorite:user:";
    private static final String LIKE_KEY_PREFIX = "behavior:like:user:";
    private static final String SHARE_KEY_PREFIX = "behavior:share:user:";
    private static final String CART_KEY_PREFIX = "behavior:cart:user:";
    private static final String SYNC_QUEUE_KEY = "behavior:sync:queue";
    private static final String STATS_KEY_PREFIX = "behavior:stats:item:";

    /**
     * 缓存用户行为到Redis
     */
    public void cacheBehavior(BehaviorDTO dto) {
        String key = buildKey(dto.getBehaviorType(), dto.getUserId());
        
        switch (dto.getBehaviorType()) {
            case VIEW:
                // ZSet存储浏览历史，score为时间戳，保留最近100条
                redisTemplate.opsForZSet().add(
                    key, 
                    dto.getItemId().toString(), 
                    System.currentTimeMillis()
                );
                // 只保留最近100条
                redisTemplate.opsForZSet().removeRange(key, 0, -101);
                // 设置过期时间30天
                redisTemplate.expire(key, 30, TimeUnit.DAYS);
                break;
                
            case FAVORITE:
            case LIKE:
            case SHARE:
            case CART:
                // Set存储收藏/点赞/分享/加购
                redisTemplate.opsForSet().add(key, dto.getItemId().toString());
                // 设置过期时间90天
                redisTemplate.expire(key, 90, TimeUnit.DAYS);
                break;
        }
        
        // 更新商品统计
        updateItemStats(dto.getItemId(), dto.getBehaviorType());
    }

    /**
     * 更新商品统计（Redis Hash）
     */
    private void updateItemStats(Long itemId, BehaviorType behaviorType) {
        String key = STATS_KEY_PREFIX + itemId;
        String field = behaviorType.name().toLowerCase() + "_count";
        redisTemplate.opsForHash().increment(key, field, 1);
        // 设置过期时间7天
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    /**
     * 获取商品统计
     */
    public BehaviorStatsVO getItemStats(Long itemId) {
        String key = STATS_KEY_PREFIX + itemId;
        BehaviorStatsVO vo = new BehaviorStatsVO();
        vo.setItemId(itemId);
        vo.setViewCount(getHashValue(key, "view_count"));
        vo.setFavoriteCount(getHashValue(key, "favorite_count"));
        vo.setLikeCount(getHashValue(key, "like_count"));
        vo.setShareCount(getHashValue(key, "share_count"));
        vo.setCartCount(getHashValue(key, "cart_count"));
        return vo;
    }

    private Integer getHashValue(String key, String field) {
        Object value = redisTemplate.opsForHash().get(key, field);
        return value == null ? 0 : Integer.parseInt(value.toString());
    }

    /**
     * 检查用户是否有某种行为
     */
    public Boolean checkBehavior(Long userId, Long itemId, BehaviorType behaviorType) {
        String key = buildKey(behaviorType, userId);
        
        if (behaviorType == BehaviorType.VIEW) {
            Double score = redisTemplate.opsForZSet().score(key, itemId.toString());
            return score != null;
        } else {
            return redisTemplate.opsForSet().isMember(key, itemId.toString());
        }
    }

    /**
     * 加入待刷写队列
     */
    public void pushToSyncQueue(BehaviorDTO dto) {
        String json = JSONUtil.toJsonStr(dto);
        redisTemplate.opsForList().rightPush(SYNC_QUEUE_KEY, json);
    }

    /**
     * 批量获取待刷写数据
     */
    public List<BehaviorDTO> popSyncQueue(int batchSize) {
        List<String> jsonList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            String json = redisTemplate.opsForList().leftPop(SYNC_QUEUE_KEY);
            if (StrUtil.isBlank(json)) {
                break;
            }
            jsonList.add(json);
        }
        
        return jsonList.stream()
                .map(json -> JSONUtil.toBean(json, BehaviorDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户浏览历史（从Redis）
     */
    public List<Long> getViewHistory(Long userId, int size) {
        String key = VIEW_KEY_PREFIX + userId;
        Set<String> itemIds = redisTemplate.opsForZSet().reverseRange(key, 0, size - 1);
        if (itemIds == null || itemIds.isEmpty()) {
            return new ArrayList<>();
        }
        return itemIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * 构建Redis Key
     */
    private String buildKey(BehaviorType behaviorType, Long userId) {
        switch (behaviorType) {
            case VIEW:
                return VIEW_KEY_PREFIX + userId;
            case FAVORITE:
                return FAVORITE_KEY_PREFIX + userId;
            case LIKE:
                return LIKE_KEY_PREFIX + userId;
            case SHARE:
                return SHARE_KEY_PREFIX + userId;
            case CART:
                return CART_KEY_PREFIX + userId;
            default:
                throw new IllegalArgumentException("不支持的行为类型: " + behaviorType);
        }
    }
}

