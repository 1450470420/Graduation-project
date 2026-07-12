package com.xypt.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁工具类
 * 基于 Redis SETNX 实现，适用于抢单等高并发场景
 *
 * 原理：
 *   SET key value NX EX timeout
 *   - NX: 只有 key 不存在时才设置（原子操作，天然防并发）
 *   - EX: 超时自动释放，防止锁持有者宕机后锁永不释放
 */
@Component
public class RedisLockUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey   锁的 key，如 "order:grab:12345"
     * @param lockValue 锁的持有者标识，如 courierId（用于安全释放）
     * @param timeout   锁的超时时间（秒），防止宕机死锁
     * @return true=获取成功，false=已被其他进程持有
     */
    public boolean tryLock(String lockKey, String lockValue, long timeout) {
        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, timeout, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 安全释放锁（只释放自己持有的锁，避免误删他人锁）
     *
     * 场景：业务耗时超过锁TTL，锁已自动过期并被他人获取
     * 此时不应删除他人的锁，需先校验 value
     *
     * @param lockKey   锁的 key
     * @param lockValue 当前持有者标识
     * @return true=释放成功，false=锁已不属于当前持有者
     */
    public boolean releaseLock(String lockKey, String lockValue) {
        String current = redisTemplate.opsForValue().get(lockKey);
        if (lockValue.equals(current)) {
            redisTemplate.delete(lockKey);
            return true;
        }
        return false;
    }

    /**
     * 查看锁是否存在
     */
    public boolean isLocked(String lockKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    /**
     * 获取锁剩余 TTL（秒），用于监控
     */
    public long getLockTtl(String lockKey) {
        Long ttl = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
        return ttl != null ? ttl : -2L;
    }
}
