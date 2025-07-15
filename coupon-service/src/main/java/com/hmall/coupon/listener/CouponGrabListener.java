package com.hmall.coupon.listener;

import com.alibaba.fastjson.JSON;
import com.hmall.coupon.constants.MQConstants;
import com.hmall.coupon.constants.RedisConstants;
import com.hmall.coupon.domain.dto.CouponGrabMessage;
import com.hmall.coupon.domain.dto.CouponRecord;
import com.hmall.coupon.service.IUserCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 抢券消费者监听器
 * 负责异步落库
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MQConstants.COUPON_GRAB_TOPIC,
        consumerGroup = MQConstants.COUPON_GRAB_CONSUMER_GROUP
)
public class CouponGrabListener implements RocketMQListener<CouponGrabMessage> {
    
    private final IUserCouponService userCouponService;
    private final StringRedisTemplate redisTemplate;
    
    @Override
    public void onMessage(CouponGrabMessage message) {
        Long couponId = message.getCouponId();
        Long userId = message.getUserId();
        
        try {
            log.info("收到抢券消息: couponId={}, userId={}", couponId, userId);
            
            // 1. 从Redis读取完整的抢券记录
            String recordKey = RedisConstants.COUPON_RECORD_KEY + couponId + ":" + userId;
            String recordJson = redisTemplate.opsForValue().get(recordKey);
            
            if (recordJson == null) {
                log.warn("Redis中找不到抢券记录: couponId={}, userId={}", couponId, userId);
                return;
            }
            
            CouponRecord record = JSON.parseObject(recordJson, CouponRecord.class);
            
            // 2. 幂等性判断：检查是否已落库
            if (record.getStatus() != null && record.getStatus() == 1) {
                log.info("记录已落库，跳过: couponId={}, userId={}", couponId, userId);
                return;
            }
            
            // 3. 落库到MySQL（幂等操作）
            boolean success = userCouponService.saveGrabRecord(
                    record.getCouponId(),
                    record.getUserId(),
                    record.getTimestamp()
            );
            
            if (success) {
                // 4. 更新Redis状态为"已落库"
                record.setStatus(1);
                redisTemplate.opsForValue().set(
                        recordKey,
                        JSON.toJSONString(record),
                        RedisConstants.COUPON_RECORD_EXPIRE,
                        java.util.concurrent.TimeUnit.SECONDS
                );
                
                // 5. 从待落库队列中移除
                String pendingKey = couponId + ":" + userId;
                redisTemplate.opsForZSet().remove(RedisConstants.COUPON_PENDING_KEY, pendingKey);
                
                log.info("落库成功: couponId={}, userId={}", couponId, userId);
            } else {
                log.error("落库失败: couponId={}, userId={}", couponId, userId);
                throw new RuntimeException("落库失败，触发MQ重试");
            }
            
        } catch (Exception e) {
            log.error("消费消息失败: couponId={}, userId={}", couponId, userId, e);
            throw new RuntimeException("消费失败，触发重试", e); // RocketMQ会重试
        }
    }
}

