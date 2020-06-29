package cn.mwee.base_common.mq.activemq.exception;

import cn.mwee.base_common.utils.log4j2.MwLogger;
import org.slf4j.Logger;
import org.springframework.util.ErrorHandler;

/**
 * Created by liaomengge on 2019/1/14.
 */
public class DefaultErrorHandler implements ErrorHandler {

    private static final Logger logger = MwLogger.getInstance(DefaultErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        logger.warn("Unexpected error occurred", t);
    }
}
