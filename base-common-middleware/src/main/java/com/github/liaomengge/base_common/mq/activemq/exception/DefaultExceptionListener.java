package com.github.liaomengge.base_common.mq.activemq.exception;

import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import org.slf4j.Logger;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/**
 * Created by liaomengge on 2019/1/14.
 */
public class DefaultExceptionListener implements ExceptionListener {

    private static final Logger log = LyLogger.getInstance(DefaultExceptionListener.class);

    @Override
    public void onException(JMSException e) {
        log.warn("JMSException error occurred", e);
    }
}
