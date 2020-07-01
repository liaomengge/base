package cn.ly.base_common.helper.lock.distributed;

import cn.ly.base_common.utils.log4j2.LyLogger;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 17/12/19.
 */
public interface DistributedLocker {

    Logger logger = LyLogger.getInstance(DistributedLocker.class);

    String REDIS_LOCKER_PREFIX = "lock:";
    String ZK_LOCKER_PREFIX = "/curator";
    long DEFAULT_TIMEOUT = 5;
    TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
}
