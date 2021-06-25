package com.github.liaomengge.base_common.mq.activemq.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

/**
 * Created by liaomengge on 2019/1/14.
 */
@Slf4j
public class DefaultErrorHandler implements ErrorHandler {
    
    @Override
    public void handleError(Throwable t) {
        log.warn("Unexpected error occurred", t);
    }
}
