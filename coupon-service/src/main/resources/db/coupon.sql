-- 优惠券数据库
CREATE DATABASE IF NOT EXISTS `hm-coupon` DEFAULT CHARACTER SET utf8mb4;
USE `hm-coupon`;

-- 优惠券表
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
    `name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    `type` TINYINT(1) NOT NULL COMMENT '类型：1-满减券 2-折扣券 3-随机金额券',
    `discount_amount` INT(11) DEFAULT NULL COMMENT '优惠金额（分）',
    `discount_rate` INT(3) DEFAULT NULL COMMENT '折扣率（例如：85表示8.5折）',
    `min_amount` INT(11) DEFAULT 0 COMMENT '最低消费金额（分）',
    `total_stock` INT(11) NOT NULL COMMENT '总库存',
    `available_stock` INT(11) NOT NULL COMMENT '可用库存',
    `per_user_limit` INT(3) DEFAULT 1 COMMENT '每人限领数量',
    `begin_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `valid_days` INT(5) DEFAULT 7 COMMENT '领取后有效天数',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-未开始 2-进行中 3-已结束 4-已暂停',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_begin_time` (`begin_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 用户优惠券表
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `coupon_id` BIGINT(20) NOT NULL COMMENT '优惠券ID',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-未使用 2-已使用 3-已过期',
    `receive_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    `use_time` DATETIME DEFAULT NULL COMMENT '使用时间',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `order_id` BIGINT(20) DEFAULT NULL COMMENT '使用的订单ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_coupon` (`user_id`, `coupon_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_coupon_id` (`coupon_id`),
    KEY `idx_status` (`status`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- 插入测试数据
INSERT INTO `coupon` (`id`, `name`, `type`, `discount_amount`, `discount_rate`, `min_amount`, `total_stock`, `available_stock`, `per_user_limit`, `begin_time`, `end_time`, `valid_days`, `status`)
VALUES
(1, '新用户专享满100减20', 1, 2000, NULL, 10000, 10000, 10000, 1, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 30, 2),
(2, '全场8.5折优惠券', 2, NULL, 85, 5000, 5000, 5000, 2, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 15, 2),
(3, '限时秒杀满200减50', 1, 5000, NULL, 20000, 1000, 1000, 1, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 7, 2);

