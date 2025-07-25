package com.hmall.behavior.controller;

import com.hmall.behavior.domain.dto.BehaviorDTO;
import com.hmall.behavior.domain.dto.FootprintQueryDTO;
import com.hmall.behavior.domain.vo.BehaviorStatsVO;
import com.hmall.behavior.domain.vo.FootprintVO;
import com.hmall.behavior.service.IBehaviorStatisticsService;
import com.hmall.behavior.service.IUserBehaviorService;
import com.hmall.common.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户行为控制器
 *
 * @author binZhang
 */
@Api(tags = "用户行为管理")
@RestController
@RequestMapping("/behaviors")
@RequiredArgsConstructor
public class BehaviorController {

    private final IUserBehaviorService behaviorService;
    private final IBehaviorStatisticsService statisticsService;

    @ApiOperation("记录用户行为")
    @PostMapping("/record")
    public String recordBehavior(@RequestBody BehaviorDTO dto) {
        behaviorService.recordBehavior(dto);
        return "记录成功";
    }

    @ApiOperation("查询用户足迹")
    @GetMapping("/footprint")
    public List<FootprintVO> getFootprint(FootprintQueryDTO query) {
        Long userId = UserContext.getUser();
        return behaviorService.getUserFootprint(userId, query);
    }

    @ApiOperation("查询商品行为统计")
    @GetMapping("/stats/{itemId}")
    public BehaviorStatsVO getItemStats(
            @ApiParam("商品ID") @PathVariable Long itemId) {
        return statisticsService.getItemStats(itemId);
    }

    @ApiOperation("检查用户是否对商品有某种行为")
    @GetMapping("/check")
    public Boolean checkBehavior(
            @ApiParam("商品ID") @RequestParam Long itemId,
            @ApiParam("行为类型：1-浏览，2-收藏，3-点赞，4-分享，5-加购") @RequestParam Integer behaviorType) {
        Long userId = UserContext.getUser();
        return behaviorService.checkBehavior(userId, itemId, behaviorType);
    }
}

