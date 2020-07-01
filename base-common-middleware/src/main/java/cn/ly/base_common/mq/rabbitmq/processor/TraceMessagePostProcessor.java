package cn.ly.base_common.mq.rabbitmq.processor;

import cn.ly.base_common.mq.consts.MQConst;
import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import cn.ly.base_common.utils.misc.LyIdGeneratorUtil;
import cn.ly.base_common.utils.trace.LyTraceLogUtil;
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
        String traceId = StringUtils.defaultIfBlank(LyTraceLogUtil.get(), LyIdGeneratorUtil.uuid());
        MessageProperties messageProperties = message.getMessageProperties();
        messageProperties.setHeader(MQConst.MQ_TRACE_ID, traceId);
        messageProperties.setHeader(MQConst.MQ_SEND_TIME, LyJdk8DateUtil.getMilliSecondsTime());
        return message;
    }
}
