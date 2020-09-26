package cn.ly.base_common.mq.activemq.exception;

import cn.ly.base_common.utils.log4j2.LyLogger;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.slf4j.Logger;

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
