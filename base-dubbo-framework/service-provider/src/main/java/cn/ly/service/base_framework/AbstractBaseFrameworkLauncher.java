package cn.ly.service.base_framework;

import cn.ly.base_common.utils.log4j2.LyLogger;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 16/9/14.
 */
public abstract class AbstractBaseFrameworkLauncher {

    protected static final Logger logger = LyLogger.getInstance(AbstractBaseFrameworkLauncher.class);
    protected static final byte[] lifeCycleLock = new byte[0];

}
