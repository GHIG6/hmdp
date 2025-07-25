package com.hmall.behavior.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.behavior.domain.dto.BehaviorDTO;
import com.hmall.behavior.domain.dto.FootprintQueryDTO;
import com.hmall.behavior.domain.po.UserBehavior;
import com.hmall.behavior.domain.vo.FootprintVO;

import java.util.List;

/**
 * 用户行为服务接口
 *
 * @author binZhang
 */
public interface IUserBehaviorService extends IService<UserBehavior> {

    /**
     * 记录用户行为
     *
     * @param dto 行为数据
     */
    void recordBehavior(BehaviorDTO dto);

    /**
     * 查询用户足迹
     *
     * @param userId 用户ID
     * @param query 查询条件
     * @return 足迹列表
     */
    List<FootprintVO> getUserFootprint(Long userId, FootprintQueryDTO query);

    /**
     * 批量保存用户行为（用于定时任务）
     *
     * @param behaviors 行为列表
     */
    void batchSave(List<BehaviorDTO> behaviors);

    /**
     * 检查用户是否对商品有某种行为
     *
     * @param userId 用户ID
     * @param itemId 商品ID
     * @param behaviorType 行为类型
     * @return true-已有该行为，false-未有该行为
     */
    Boolean checkBehavior(Long userId, Long itemId, Integer behaviorType);
}

