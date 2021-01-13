package com.github.liaomengge.base_common.sentinel.handler;

import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 2021/1/12.
 */
public abstract class AbstractSentinelHandler {

    protected static final Logger log = LyLogger.getInstance(AbstractSentinelHandler.class);

    public void init() {
        try {
            doInit();
        } catch (Exception e) {
            log.error("[" + this.getClass().getSimpleName() + "] handler doInit fail", e);
        }
    }

    public abstract void doInit() throws Exception;
}
