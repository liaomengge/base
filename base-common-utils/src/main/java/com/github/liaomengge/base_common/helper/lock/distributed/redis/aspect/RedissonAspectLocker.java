package com.github.liaomengge.base_common.helper.lock.distributed.redis.aspect;

import com.github.liaomengge.base_common.helper.lock.distributed.callback.AcquiredLockCallback;
import com.github.liaomengge.base_common.helper.lock.distributed.consts.DistributedConst;
import com.github.liaomengge.base_common.helper.lock.distributed.redis.RedissonLocker;
import com.github.liaomengge.base_common.helper.lock.distributed.redis.aspect.annotation.RedissionLock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.scheduling.annotation.Async;

/**
 * Created by liaomengge on 2021/6/2.
 */
@Slf4j
@Async
@AllArgsConstructor
public class RedissonAspectLocker {
    
    private final RedissonLocker redissonLocker;

    @Around("@annotation(redissionLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissionLock redissionLock) {
        String lockName = redissionLock.value();
        long leaseTime = redissionLock.leaseTime();
        return redissonLocker.tryLock(lockName, leaseTime, DistributedConst.DEFAULT_TIME_UNIT,
                new AcquiredLockCallback<Object>() {

                    @Override
                    public Object onFailure() {
                        log.warn("获取Redis分布式锁失败");
                        return null;
                    }

                    @Override
                    public Object onFailure(Throwable throwable) {
                        log.warn("获取Redis分布式锁失败", throwable);
                        return null;
                    }

                    @Override
                    public Object onSuccess() {
                        try {
                            return joinPoint.proceed();
                        } catch (Throwable throwable) {
                            log.error("Redission Aspect Handler Fail", throwable);
                        }
                        return null;
                    }
                });
    }
}
