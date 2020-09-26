package cn.ly.base_common.mq.activemq.exception;

import cn.ly.base_common.utils.log4j2.LyLogger;

import org.slf4j.Logger;
import org.springframework.util.ErrorHandler;

/**
 * Created by liaomengge on 2019/1/14.
 */
public class DefaultErrorHandler implements ErrorHandler {

    private static final Logger log = LyLogger.getInstance(DefaultErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        log.warn("Unexpected error occurred", t);
    }
}
