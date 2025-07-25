package com.hmall.behavior.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品行为统计实体
 *
 * @author binZhang
 */
@Data
@TableName("behavior_statistics")
public class BehaviorStatistics {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 收藏次数
     */
    private Integer favoriteCount;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 分享次数
     */
    private Integer shareCount;

    /**
     * 加购次数
     */
    private Integer cartCount;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

