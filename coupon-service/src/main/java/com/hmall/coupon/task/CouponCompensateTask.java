package com.hmall.coupon.task;

import com.alibaba.fastjson.JSON;
import com.hmall.coupon.constants.MQConstants;
import com.hmall.coupon.constants.RedisConstants;
import com.hmall.coupon.domain.dto.CouponGrabMessage;
import com.hmall.coupon.domain.dto.CouponRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 优惠券补偿定时任务
 * 负责扫描待落库队列，补偿MQ发送失败或消费失败的记录
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponCompensateTask {
    
    private final StringRedisTemplate redisTemplate;
    private final RocketMQTemplate rocketMQTemplate;
    
    /**
     * 每分钟扫描一次待落库队列，补偿失败的记录
     */
    @Scheduled(cron = "0 * * * * ?")
    public void compensatePendingRecords() {
        try {
            log.info("开始执行优惠券补偿任务...");
            
            // 1. 从Sorted Set中获取5分钟前的记录（超时未落库）
            long fiveMinutesAgo = System.currentTimeMillis() - 5 * 60 * 1000;
            Set<String> pendingRecords = redisTemplate.opsForZSet()
                    .rangeByScore(RedisConstants.COUPON_PENDING_KEY, 0, fiveMinutesAgo);
            
            if (pendingRecords == null || pendingRecords.isEmpty()) {
                log.info("没有需要补偿的记录");
                return;
            }
            
            log.info("发现{}条待补偿记录", pendingRecords.size());
            
            // 2. 遍历补偿
            int successCount = 0;
            int skipCount = 0;
            
            for (String record : pendingRecords) {
                try {
                    String[] parts = record.split(":");
                    if (parts.length != 2) {
                        log.warn("记录格式错误：{}", record);
                        continue;
                    }
                    
                    Long couponId = Long.parseLong(parts[0]);
                    Long userId = Long.parseLong(parts[1]);
                    
                    // 检查Redis中的状态
                    String recordKey = RedisConstants.COUPON_RECORD_KEY + couponId + ":" + userId;
                    String recordJson = redisTemplate.opsForValue().get(recordKey);
                    
                    if (recordJson == null) {
                        // Redis中已没有记录，从待落库队列移除
                        log.info("Redis中已无记录，移除：couponId={}, userId={}", couponId, userId);
                        redisTemplate.opsForZSet().remove(RedisConstants.COUPON_PENDING_KEY, record);
                        skipCount++;
                        continue;
                    }
                    
                    CouponRecord couponRecord = JSON.parseObject(recordJson, CouponRecord.class);
                    
                    if (couponRecord.getStatus() != null && couponRecord.getStatus() == 1) {
                        // 已落库，从待落库队列移除
                        log.info("记录已落库，移除：couponId={}, userId={}", couponId, userId);
                        redisTemplate.opsForZSet().remove(RedisConstants.COUPON_PENDING_KEY, record);
                        skipCount++;
                        continue;
                    }
                    
                    // 3. 重新发送MQ消息
                    log.info("补偿发送MQ: couponId={}, userId={}", couponId, userId);
                    CouponGrabMessage message = new CouponGrabMessage();
                    message.setCouponId(couponId);
                    message.setUserId(userId);
                    message.setTimestamp(couponRecord.getTimestamp());
                    
                    rocketMQTemplate.syncSend(MQConstants.COUPON_GRAB_TOPIC, message);
                    successCount++;
                    
                } catch (Exception e) {
                    log.error("补偿单条记录失败：{}", record, e);
                }
            }
            
            log.info("优惠券补偿任务完成，成功：{}, 跳过：{}", successCount, skipCount);
            
        } catch (Exception e) {
            log.error("补偿任务执行失败", e);
        }
    }
    
    /**
     * 每天凌晨2点预加载活动中的优惠券到Redis
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void preloadActiveCoupons() {
        log.info("开始预加载活动中的优惠券到Redis...");
        try {
            // 这里可以注入ICouponService来执行预加载
            // 为了避免循环依赖，这里仅作示例
            log.info("预加载活动优惠券完成");
        } catch (Exception e) {
            log.error("预加载任务失败", e);
        }
    }
}

