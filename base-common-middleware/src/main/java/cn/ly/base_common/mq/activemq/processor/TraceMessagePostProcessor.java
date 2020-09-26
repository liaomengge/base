package cn.ly.base_common.mq.activemq.processor;

import cn.ly.base_common.mq.consts.MQConst;
import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import cn.ly.base_common.utils.misc.LyIdGeneratorUtil;
import cn.ly.base_common.utils.trace.LyTraceLogUtil;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.MessagePostProcessor;

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
