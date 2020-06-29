package cn.mwee.base_common.mq.activemq.processor;

import cn.mwee.base_common.mq.consts.MQConst;
import cn.mwee.base_common.utils.date.MwJdk8DateUtil;
import cn.mwee.base_common.utils.misc.MwIdGeneratorUtil;
import cn.mwee.base_common.utils.trace.MwTraceLogUtil;
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
        String traceId = StringUtils.defaultIfBlank(MwTraceLogUtil.get(), MwIdGeneratorUtil.uuid());
        message.setStringProperty(MQConst.MQ_TRACE_ID, traceId);
        message.setLongProperty(MQConst.MQ_SEND_TIME, MwJdk8DateUtil.getMilliSecondsTime());
        return message;
    }
}
