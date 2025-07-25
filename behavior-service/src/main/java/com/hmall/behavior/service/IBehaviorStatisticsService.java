package com.hmall.behavior.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.behavior.domain.po.BehaviorStatistics;
import com.hmall.behavior.domain.vo.BehaviorStatsVO;
import com.hmall.behavior.enums.BehaviorType;

/**
 * 行为统计服务接口
 *
 * @author binZhang
 */
public interface IBehaviorStatisticsService extends IService<BehaviorStatistics> {

    /**
     * 增加商品行为统计
     *
     * @param itemId 商品ID
     * @param behaviorType 行为类型
     */
    void incrementStats(Long itemId, BehaviorType behaviorType);

    /**
     * 获取商品行为统计
     *
     * @param itemId 商品ID
     * @return 统计数据
     */
    BehaviorStatsVO getItemStats(Long itemId);
}

