package com.hmall.search.service;

import java.io.IOException;

/**
 * 数据同步服务接口
 */
public interface IDataSyncService {
    
    /**
     * 全量同步商品数据从MySQL到ES
     * 用于首次部署或数据重建
     */
    void fullSync() throws IOException;
}

