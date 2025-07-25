package com.hmall.behavior.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.behavior.domain.po.BehaviorStatistics;
import com.hmall.behavior.domain.vo.BehaviorStatsVO;
import com.hmall.behavior.enums.BehaviorType;
import com.hmall.behavior.mapper.BehaviorStatisticsMapper;
import com.hmall.behavior.service.IBehaviorStatisticsService;
import com.hmall.behavior.service.cache.BehaviorCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 行为统计服务实现
 *
 * @author binZhang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BehaviorStatisticsServiceImpl extends ServiceImpl<BehaviorStatisticsMapper, BehaviorStatistics> implements IBehaviorStatisticsService {

    private final BehaviorCacheService cacheService;

    @Override
    public void incrementStats(Long itemId, BehaviorType behaviorType) {
        // 更新数据库统计
        BehaviorStatistics stats = lambdaQuery()
                .eq(BehaviorStatistics::getItemId, itemId)
                .one();
        
        if (stats == null) {
            stats = new BehaviorStatistics();
            stats.setItemId(itemId);
            stats.setViewCount(0);
            stats.setFavoriteCount(0);
            stats.setLikeCount(0);
            stats.setShareCount(0);
            stats.setCartCount(0);
        }
        
        // 根据行为类型增加对应计数
        switch (behaviorType) {
            case VIEW:
                stats.setViewCount(stats.getViewCount() + 1);
                break;
            case FAVORITE:
                stats.setFavoriteCount(stats.getFavoriteCount() + 1);
                break;
            case LIKE:
                stats.setLikeCount(stats.getLikeCount() + 1);
                break;
            case SHARE:
                stats.setShareCount(stats.getShareCount() + 1);
                break;
            case CART:
                stats.setCartCount(stats.getCartCount() + 1);
                break;
        }
        
        stats.setUpdateTime(LocalDateTime.now());
        saveOrUpdate(stats);
    }

    @Override
    public BehaviorStatsVO getItemStats(Long itemId) {
        // 优先从Redis获取
        BehaviorStatsVO vo = cacheService.getItemStats(itemId);
        
        // 如果Redis没有数据，从数据库获取
        if (vo.getViewCount() == 0 && vo.getFavoriteCount() == 0 && vo.getLikeCount() == 0) {
            BehaviorStatistics stats = lambdaQuery()
                    .eq(BehaviorStatistics::getItemId, itemId)
                    .one();
            if (stats != null) {
                vo = BeanUtil.copyProperties(stats, BehaviorStatsVO.class);
            }
        }
        
        return vo;
    }
}

