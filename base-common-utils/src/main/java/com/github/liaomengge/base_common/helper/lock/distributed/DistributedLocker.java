package com.github.liaomengge.base_common.helper.lock.distributed;

import com.github.liaomengge.base_common.utils.log4j2.LyLogger;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

/**
 * Created by liaomengge on 17/12/19.
 */
public interface DistributedLocker {

    Logger log = LyLogger.getInstance(DistributedLocker.class);

    String REDIS_LOCKER_PREFIX = "lock:";
    String ZK_LOCKER_PREFIX = "/lock/";
    long DEFAULT_TIMEOUT = 5;
    TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
}