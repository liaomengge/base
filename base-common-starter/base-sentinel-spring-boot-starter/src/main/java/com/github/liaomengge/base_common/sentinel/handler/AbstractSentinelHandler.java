package com.github.liaomengge.base_common.sentinel.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liaomengge on 2021/1/12.
 */
public abstract class AbstractSentinelHandler {

    protected static final Logger log = LoggerFactory.getLogger(AbstractSentinelHandler.class);

    public void init() {
        try {
            doInit();
        } catch (Exception e) {
            log.error("[{}] handler doInit fail", this.getClass().getSimpleName(), e);
        }
    }

    public abstract void doInit() throws Exception;
}
