package com.github.liaomengge.base_common.helper.lock;

import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 17/12/19.
 */
public interface DistributedLocker {
    Logger log = LyLogger.getInstance(DistributedLocker.class);
}
