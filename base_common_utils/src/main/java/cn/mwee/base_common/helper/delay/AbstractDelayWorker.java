package cn.mwee.base_common.helper.delay;

import cn.mwee.base_common.helper.redis.JedisClusterHelper;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import lombok.Setter;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 18/1/9.
 */
public abstract class AbstractDelayWorker {

    protected static final Logger logger = MwLogger.getInstance(AbstractDelayWorker.class);

    protected final String BASE_DELAY_PREFIX = "delay:";

    protected String delayPrefix;

    @Setter
    protected JedisClusterHelper jedisClusterHelper;
}
