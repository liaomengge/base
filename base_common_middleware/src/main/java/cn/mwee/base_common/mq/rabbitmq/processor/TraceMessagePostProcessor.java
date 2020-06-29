package cn.mwee.base_common.mq.rabbitmq.processor;

import cn.mwee.base_common.mq.consts.MQConst;
import cn.mwee.base_common.utils.date.MwJdk8DateUtil;
import cn.mwee.base_common.utils.misc.MwIdGeneratorUtil;
import cn.mwee.base_common.utils.trace.MwTraceLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

/**
 * Created by liaomengge on 2019/11/18.
 */
public class TraceMessagePostProcessor implements MessagePostProcessor {

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        String traceId = StringUtils.defaultIfBlank(MwTraceLogUtil.get(), MwIdGeneratorUtil.uuid());
        MessageProperties messageProperties = message.getMessageProperties();
        messageProperties.setHeader(MQConst.MQ_TRACE_ID, traceId);
        messageProperties.setHeader(MQConst.MQ_SEND_TIME, MwJdk8DateUtil.getMilliSecondsTime());
        return message;
    }
}
