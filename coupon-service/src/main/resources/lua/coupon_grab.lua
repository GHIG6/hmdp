-- 抢券Lua脚本
-- KEYS[1]: couponId
-- KEYS[2]: userId
-- ARGV[1]: timestamp
-- ARGV[2]: perUserLimit

local couponId = KEYS[1]
local userId = KEYS[2]
local timestamp = ARGV[1]
local perUserLimit = tonumber(ARGV[2])

-- Key定义
local stockKey = "coupon:stock:" .. couponId
local infoKey = "coupon:info:" .. couponId
local limitKey = "coupon:limit:" .. couponId .. ":" .. userId
local recordKey = "coupon:record:" .. couponId .. ":" .. userId

-- 1. 检查库存（防止为nil）
local stock = redis.call('GET', stockKey)
if not stock or tonumber(stock) <= 0 then
    return -1  -- 库存不足
end

-- 2. 检查是否已抢过（防止重复）
local alreadyGrabbed = redis.call('EXISTS', recordKey)
if alreadyGrabbed == 1 then
    return -3  -- 已经抢过了
end

-- 3. 检查用户限领次数
local userCount = redis.call('GET', limitKey)
if userCount and tonumber(userCount) >= perUserLimit then
    return -2  -- 超过限领次数
end

-- 4. 原子操作：扣库存 + 记录用户抢券
redis.call('DECR', stockKey)
redis.call('INCR', limitKey)
redis.call('EXPIRE', limitKey, 86400 * 30)  -- 30天过期

-- 5. 关键：将完整的抢券记录写入Redis（用于后续落库）
local recordValue = cjson.encode({
    couponId = couponId,
    userId = userId,
    timestamp = timestamp,
    status = 0  -- 0-待落库 1-已落库
})
redis.call('SETEX', recordKey, 86400, recordValue)  -- 24小时过期

-- 6. 加入待落库队列（Sorted Set，按时间排序）
redis.call('ZADD', 'coupon:pending', timestamp, couponId .. ':' .. userId)

return 1  -- 成功

