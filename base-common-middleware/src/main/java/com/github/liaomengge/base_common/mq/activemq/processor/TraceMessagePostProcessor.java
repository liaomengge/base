package com.github.liaomengge.base_common.mq.activemq.processor;

import com.github.liaomengge.base_common.mq.consts.MQConst;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.misc.LyIdGeneratorUtil;
import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.MessagePostProcessor;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by liaomengge on 2019/11/18.
 */
public class TraceMessagePostProcessor implements MessagePostProcessor {

    @Override
    public Message postProcessMessage(Message message) throws JMSException {
        String traceId = StringUtils.defaultIfBlank(LyTraceLogUtil.get(), LyIdGeneratorUtil.uuid());
        message.setStringProperty(MQConst.MQ_TRACE_ID, traceId);
        message.setLongProperty(MQConst.MQ_SEND_TIME, LyJdk8DateUtil.getMilliSecondsTime());
        return message;
    }
}
