package com.hmall.api.client;

import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.api.dto.BehaviorDTO;
import com.hmall.api.vo.BehaviorStatsVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户行为服务 Feign 客户端
 *
 * @author binZhang
 */
@FeignClient(value = "behavior-service", configuration = DefaultFeignConfig.class)
public interface BehaviorClient {

    /**
     * 记录用户行为
     *
     * @param dto 行为数据
     * @return 记录结果
     */
    @PostMapping("/behaviors/record")
    String recordBehavior(@RequestBody BehaviorDTO dto);

    /**
     * 查询商品行为统计
     *
     * @param itemId 商品ID
     * @return 统计数据
     */
    @GetMapping("/behaviors/stats/{itemId}")
    BehaviorStatsVO getItemStats(@PathVariable("itemId") Long itemId);

    /**
     * 检查用户是否对商品有某种行为
     *
     * @param itemId 商品ID
     * @param behaviorType 行为类型：1-浏览，2-收藏，3-点赞，4-分享，5-加购
     * @return true-已有该行为，false-未有该行为
     */
    @GetMapping("/behaviors/check")
    Boolean checkBehavior(@RequestParam("itemId") Long itemId, 
                         @RequestParam("behaviorType") Integer behaviorType);
}

