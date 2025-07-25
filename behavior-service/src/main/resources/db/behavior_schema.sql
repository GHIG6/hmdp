-- ============================
-- 用户行为服务数据库初始化脚本
-- ============================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `hm-behavior` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `hm-behavior`;

-- ============================
-- 用户行为表
-- ============================
DROP TABLE IF EXISTS `user_behavior`;
CREATE TABLE `user_behavior` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `item_id` BIGINT NOT NULL COMMENT '商品ID',
    `behavior_type` TINYINT NOT NULL COMMENT '行为类型：1-浏览，2-收藏，3-点赞，4-分享，5-加购',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为发生时间',
    `extend_info` JSON COMMENT '扩展信息（JSON格式）',
    PRIMARY KEY (`id`),
    KEY `idx_user_behavior` (`user_id`, `behavior_type`, `create_time`),
    KEY `idx_item` (`item_id`, `behavior_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为表';

-- ============================
-- 商品行为统计表
-- ============================
DROP TABLE IF EXISTS `behavior_statistics`;
CREATE TABLE `behavior_statistics` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `item_id` BIGINT NOT NULL COMMENT '商品ID',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `favorite_count` INT NOT NULL DEFAULT 0 COMMENT '收藏次数',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
    `share_count` INT NOT NULL DEFAULT 0 COMMENT '分享次数',
    `cart_count` INT NOT NULL DEFAULT 0 COMMENT '加购次数',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_item` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品行为统计表';

-- ============================
-- 插入测试数据（可选）
-- ============================
-- 假设已有用户ID为1，商品ID为100001
INSERT INTO `user_behavior` (`user_id`, `item_id`, `behavior_type`, `create_time`) VALUES
(1, 100001, 1, NOW() - INTERVAL 1 DAY),
(1, 100002, 1, NOW() - INTERVAL 2 HOUR),
(1, 100003, 1, NOW() - INTERVAL 1 HOUR),
(1, 100001, 2, NOW() - INTERVAL 30 MINUTE),
(1, 100002, 3, NOW() - INTERVAL 10 MINUTE);

INSERT INTO `behavior_statistics` (`item_id`, `view_count`, `favorite_count`, `like_count`) VALUES
(100001, 150, 35, 20),
(100002, 280, 68, 42),
(100003, 95, 12, 8);

