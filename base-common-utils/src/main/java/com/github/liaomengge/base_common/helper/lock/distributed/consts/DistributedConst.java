package com.github.liaomengge.base_common.helper.lock.distributed.consts;

import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2020/10/30.
 */
public interface DistributedConst {

    String REDIS_LOCKER_PREFIX = "lock:";
    String ZK_LOCKER_PREFIX = "/lock/";

    long DEFAULT_TIMEOUT = 5;
    TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
}
