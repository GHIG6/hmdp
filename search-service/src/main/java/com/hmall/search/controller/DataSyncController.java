package com.hmall.search.controller;

import com.hmall.search.service.IDataSyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 数据同步控制器
 * 用于管理MySQL到ES的数据同步
 */
@Api(tags = "数据同步接口")
@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class DataSyncController {
    
    private final IDataSyncService dataSyncService;
    
    /**
     * 全量同步商品数据
     * 从MySQL全量导入商品数据到ES
     * 适用场景：
     * 1. 首次部署search-service
     * 2. ES数据丢失需要重建
     * 3. 索引结构变更后重新导入
     */
    @ApiOperation("全量同步商品数据")
    @PostMapping("/full")
    public String fullSync() throws IOException {
        dataSyncService.fullSync();
        return "全量同步任务已开始执行";
    }
}

