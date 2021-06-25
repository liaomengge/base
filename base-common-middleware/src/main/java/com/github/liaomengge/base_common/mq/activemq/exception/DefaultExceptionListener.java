package com.github.liaomengge.base_common.mq.activemq.exception;

import lombok.extern.slf4j.Slf4j;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/**
 * Created by liaomengge on 2019/1/14.
 */
@Slf4j
public class DefaultExceptionListener implements ExceptionListener {

    @Override
    public void onException(JMSException e) {
        log.warn("JMSException error occurred", e);
    }
}
