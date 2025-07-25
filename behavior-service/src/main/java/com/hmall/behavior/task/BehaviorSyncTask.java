package com.hmall.behavior.task;

import cn.hutool.core.collection.CollUtil;
import com.hmall.behavior.domain.dto.BehaviorDTO;
import com.hmall.behavior.service.IUserBehaviorService;
import com.hmall.behavior.service.cache.BehaviorCacheService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户行为数据同步定时任务
 * 功能：定时从 Redis 队列中批量读取用户行为数据，刷写到 MySQL
 *
 * @author binZhang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorSyncTask {

    private final BehaviorCacheService cacheService;
    private final IUserBehaviorService behaviorService;

    /**
     * 批量同步用户行为数据到MySQL
     * 执行策略：建议每分钟执行一次
     * Cron表达式：0 * * * * ?
     */
    @XxlJob("behaviorSyncJob")
    public void syncBehaviorToDb() {
        long startTime = System.currentTimeMillis();
        int totalCount = 0;
        int batchSize = 1000;
        
        try {
            log.info("开始执行用户行为数据同步任务...");
            
            while (true) {
                // 1. 从Redis队列批量获取数据
                List<BehaviorDTO> behaviors = cacheService.popSyncQueue(batchSize);
                
                if (CollUtil.isEmpty(behaviors)) {
                    log.info("队列中没有待同步的数据，任务结束");
                    break;
                }
                
                totalCount += behaviors.size();
                
                // 2. 批量插入MySQL
                try {
                    behaviorService.batchSave(behaviors);
                    log.info("成功刷写 {} 条用户行为数据", behaviors.size());
                } catch (Exception e) {
                    log.error("批量刷写失败，重新加入队列", e);
                    // 失败时重新入队（避免数据丢失）
                    for (BehaviorDTO behavior : behaviors) {
                        cacheService.pushToSyncQueue(behavior);
                    }
                    // 记录失败日志到XXL-Job
                    XxlJobHelper.log("批量刷写失败：" + e.getMessage());
                }
                
                // 3. 如果本批次数据少于批量大小，说明队列已空
                if (behaviors.size() < batchSize) {
                    break;
                }
                
                // 4. 防止长时间占用，超过50000条就停止本次任务
                if (totalCount >= 50000) {
                    log.warn("本次任务已处理 {} 条数据，达到上限，等待下次执行", totalCount);
                    break;
                }
            }
            
            long costTime = System.currentTimeMillis() - startTime;
            String result = String.format("任务完成！共刷写 %d 条数据，耗时 %d ms", totalCount, costTime);
            log.info(result);
            
            // 记录成功日志到XXL-Job
            XxlJobHelper.handleSuccess(result);
            
        } catch (Exception e) {
            log.error("用户行为数据同步任务执行失败", e);
            XxlJobHelper.handleFail("任务执行失败：" + e.getMessage());
        }
    }

    /**
     * 清理过期的用户行为数据（可选任务）
     * 执行策略：建议每天凌晨3点执行一次
     * Cron表达式：0 0 3 * * ?
     */
    @XxlJob("behaviorCleanJob")
    public void cleanExpiredBehavior() {
        try {
            log.info("开始清理过期用户行为数据...");
            
            // TODO: 实现清理逻辑，例如删除90天前的浏览记录
            // behaviorService.deleteExpiredBehaviors(90);
            
            XxlJobHelper.handleSuccess("清理任务完成");
            
        } catch (Exception e) {
            log.error("清理过期数据失败", e);
            XxlJobHelper.handleFail("清理任务失败：" + e.getMessage());
        }
    }
}

