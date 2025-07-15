package com.hmall.coupon.service.impl;

import com.hmall.coupon.constants.MQConstants;
import com.hmall.coupon.domain.dto.CouponGrabMessage;
import com.hmall.coupon.enums.CouponGrabResult;
import com.hmall.coupon.service.ICouponGrabService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 抢券服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponGrabServiceImpl implements ICouponGrabService {
    
    private final StringRedisTemplate redisTemplate;
    private final RocketMQTemplate rocketMQTemplate;
    private final DefaultRedisScript<Long> grabCouponScript;
    
    @Override
    public CouponGrabResult grabCoupon(Long couponId, Long userId) {
        // 1. 参数校验
        if (couponId == null || userId == null) {
            return CouponGrabResult.SYSTEM_ERROR;
        }
        
        // 2. 执行Lua脚本（原子操作）
        Long result = executeLuaScript(couponId, userId);
        
        // 3. 根据返回值判断结果
        CouponGrabResult grabResult = CouponGrabResult.fromCode(result);
        
        // 4. 如果Lua执行成功，发送MQ消息（异步，失败不影响用户体验）
        if (grabResult.isSuccess()) {
            sendMQMessage(couponId, userId);
        }
        
        // 5. 返回结果
        return grabResult;
    }
    
    /**
     * 执行Lua脚本
     */
    private Long executeLuaScript(Long couponId, Long userId) {
        try {
            // 获取每人限领数量（从Redis读取）
            String infoKey = "coupon:info:" + couponId;
            String perUserLimitStr = (String) redisTemplate.opsForHash().get(infoKey, "perUserLimit");
            String perUserLimit = perUserLimitStr != null ? perUserLimitStr : "1";
            
            // 准备参数
            List<String> keys = Arrays.asList(
                    String.valueOf(couponId),
                    String.valueOf(userId)
            );
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            
            // 执行Lua脚本
            return redisTemplate.execute(
                    grabCouponScript,
                    keys,
                    timestamp,
                    perUserLimit
            );
        } catch (Exception e) {
            log.error("Lua脚本执行失败, couponId:{}, userId:{}", couponId, userId, e);
            return -999L; // 系统错误
        }
    }
    
    /**
     * 发送MQ消息（尽力而为，失败有补偿机制）
     */
    private void sendMQMessage(Long couponId, Long userId) {
        try {
            CouponGrabMessage message = new CouponGrabMessage();
            message.setCouponId(couponId);
            message.setUserId(userId);
            message.setTimestamp(System.currentTimeMillis());
            
            // 异步发送，不阻塞
            rocketMQTemplate.asyncSend(
                    MQConstants.COUPON_GRAB_TOPIC,
                    message,
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("MQ发送成功: couponId={}, userId={}", couponId, userId);
                        }
                        
                        @Override
                        public void onException(Throwable e) {
                            log.error("MQ发送失败: couponId={}, userId={}, 依赖补偿机制", 
                                    couponId, userId, e);
                            // 不抛异常，依赖定时任务补偿
                        }
                    }
            );
        } catch (Exception e) {
            log.error("MQ发送异常: couponId={}, userId={}, 依赖补偿机制", 
                    couponId, userId, e);
            // 不影响用户，依赖补偿机制
        }
    }
}

