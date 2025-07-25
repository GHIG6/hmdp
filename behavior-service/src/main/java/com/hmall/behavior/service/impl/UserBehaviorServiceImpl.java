package com.hmall.behavior.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.behavior.domain.dto.BehaviorDTO;
import com.hmall.behavior.domain.dto.FootprintQueryDTO;
import com.hmall.behavior.domain.po.UserBehavior;
import com.hmall.behavior.domain.vo.FootprintVO;
import com.hmall.behavior.mapper.UserBehaviorMapper;
import com.hmall.behavior.service.IUserBehaviorService;
import com.hmall.behavior.service.cache.BehaviorCacheService;
import com.hmall.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户行为服务实现
 *
 * @author binZhang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehavior> implements IUserBehaviorService {

    private final BehaviorCacheService cacheService;

    @Override
    public void recordBehavior(BehaviorDTO dto) {
        // 1. 从上下文获取用户ID
        Long userId = UserContext.getUser();
        if (userId == null) {
            log.warn("用户未登录，无法记录行为");
            return;
        }
        
        dto.setUserId(userId);
        dto.setCreateTime(LocalDateTime.now());
        
        // 2. 写入Redis（热数据）
        cacheService.cacheBehavior(dto);
        
        // 3. 加入待刷写队列
        cacheService.pushToSyncQueue(dto);
        
        log.debug("记录用户行为: userId={}, itemId={}, behaviorType={}", 
                userId, dto.getItemId(), dto.getBehaviorType());
    }

    @Override
    public List<FootprintVO> getUserFootprint(Long userId, FootprintQueryDTO query) {
        // 计算偏移量
        int offset = (query.getPage() - 1) * query.getSize();
        
        // 查询数据库（关联商品信息）
        Integer behaviorType = query.getBehaviorType() == null ? null : query.getBehaviorType().getValue();
        List<FootprintVO> footprints = baseMapper.selectFootprintWithItem(
                userId, 
                behaviorType, 
                offset, 
                query.getSize()
        );
        
        return footprints;
    }

    @Override
    public void batchSave(List<BehaviorDTO> behaviors) {
        if (CollUtil.isEmpty(behaviors)) {
            return;
        }
        
        // 转换为实体
        List<UserBehavior> entities = behaviors.stream()
                .map(dto -> {
                    UserBehavior entity = BeanUtil.copyProperties(dto, UserBehavior.class);
                    if (entity.getCreateTime() == null) {
                        entity.setCreateTime(LocalDateTime.now());
                    }
                    return entity;
                })
                .collect(Collectors.toList());
        
        // 批量插入
        saveBatch(entities, 1000);
        
        log.info("批量保存用户行为: {} 条", entities.size());
    }

    @Override
    public Boolean checkBehavior(Long userId, Long itemId, Integer behaviorType) {
        // 先查Redis
        try {
            return cacheService.checkBehavior(
                    userId, 
                    itemId, 
                    com.hmall.behavior.enums.BehaviorType.values()[behaviorType - 1]
            );
        } catch (Exception e) {
            log.warn("Redis查询失败，降级查询数据库", e);
            // Redis失败，降级查询数据库
            return lambdaQuery()
                    .eq(UserBehavior::getUserId, userId)
                    .eq(UserBehavior::getItemId, itemId)
                    .eq(UserBehavior::getBehaviorType, behaviorType)
                    .exists();
        }
    }
}

