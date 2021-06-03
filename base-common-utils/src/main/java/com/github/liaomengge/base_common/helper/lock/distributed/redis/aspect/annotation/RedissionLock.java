package com.github.liaomengge.base_common.helper.lock.distributed.redis.aspect.annotation;

import com.github.liaomengge.base_common.helper.lock.distributed.consts.DistributedConst;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2021/6/2.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RedissionLock {

    /**
     * 分布式锁名称
     *
     * @return String
     */
    String value() default DistributedConst.REDIS_LOCKER_PREFIX + "redis";

    /**
     * 锁超时时间,默认5秒
     *
     * @return int
     */
    long leaseTime() default DistributedConst.DEFAULT_TIMEOUT;
}
