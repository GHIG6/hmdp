package com.hmall.behavior.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmall.behavior.domain.po.UserBehavior;
import com.hmall.behavior.domain.vo.FootprintVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户行为Mapper
 *
 * @author binZhang
 */
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {

    /**
     * 查询用户足迹（关联商品信息）
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param offset 偏移量
     * @param size 查询数量
     * @return 足迹列表
     */
    List<FootprintVO> selectFootprintWithItem(
            @Param("userId") Long userId,
            @Param("behaviorType") Integer behaviorType,
            @Param("offset") Integer offset,
            @Param("size") Integer size
    );
}

