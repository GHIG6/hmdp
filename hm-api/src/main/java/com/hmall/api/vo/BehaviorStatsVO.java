package com.hmall.api.vo;

import lombok.Data;

/**
 * 商品行为统计展示对象（API层）
 *
 * @author binZhang
 */
@Data
public class BehaviorStatsVO {

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
}

