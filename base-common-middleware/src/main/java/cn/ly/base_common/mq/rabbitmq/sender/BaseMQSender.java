package cn.ly.base_common.mq.rabbitmq.sender;

import cn.ly.base_common.mq.consts.MQConst.RabbitMQ;
import cn.ly.base_common.mq.rabbitmq.AbstractMQSender;
import cn.ly.base_common.mq.rabbitmq.monitor.DefaultMQMonitor;
import cn.ly.base_common.mq.rabbitmq.processor.TraceMessagePostProcessor;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.retry.support.RetryTemplate;

import java.util.List;
import java.util.Objects;

/**
 * Created by liaomengge on 2018/12/6.
 */
public abstract class BaseMQSender extends AbstractMQSender implements InitializingBean {

    protected CachingConnectionFactory cachingConnectionFactory;
    protected RabbitAdmin rabbitAdmin;
    protected RabbitTemplate rabbitTemplate;
    protected MessageConverter messageConverter;
    protected RetryTemplate retryTemplate;
    protected RabbitTemplate.ConfirmCallback confirmCallback;
    protected RabbitTemplate.ReturnCallback returnCallback;
    protected boolean mandatory;
    protected DefaultMQMonitor mqMonitor;

    public BaseMQSender(CachingConnectionFactory cachingConnectionFactory, RabbitAdmin rabbitAdmin,
                        RetryTemplate retryTemplate,
                        RabbitTemplate.ConfirmCallback confirmCallback,
                        RabbitTemplate.ReturnCallback returnCallback, boolean mandatory,
                        DefaultMQMonitor mqMonitor) {
        this(cachingConnectionFactory, rabbitAdmin, new Jackson2JsonMessageConverter(),
                Lists.newArrayList(new TraceMessagePostProcessor()),
                retryTemplate, confirmCallback, returnCallback, mandatory, mqMonitor);
    }

    public BaseMQSender(CachingConnectionFactory cachingConnectionFactory, RabbitAdmin rabbitAdmin,
                        MessageConverter messageConverter, List<MessagePostProcessor> messagePostProcessors,
                        RetryTemplate retryTemplate, RabbitTemplate.ConfirmCallback confirmCallback,
                        RabbitTemplate.ReturnCallback returnCallback, boolean mandatory,
                        DefaultMQMonitor mqMonitor) {
        this.cachingConnectionFactory = cachingConnectionFactory;
        this.rabbitAdmin = rabbitAdmin;
        this.messageConverter = messageConverter;
        this.retryTemplate = retryTemplate;
        this.confirmCallback = confirmCallback;
        this.returnCallback = returnCallback;
        this.mandatory = mandatory;
        this.mqMonitor = mqMonitor;
        rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(this.cachingConnectionFactory);
        rabbitTemplate.setRetryTemplate(this.retryTemplate);
        if (Objects.nonNull(this.messageConverter)) {
            rabbitTemplate.setMessageConverter(this.messageConverter);
        }
        if (CollectionUtils.isNotEmpty(messagePostProcessors)) {
            rabbitTemplate.setBeforePublishPostProcessors(messagePostProcessors.stream().toArray(MessagePostProcessor[]::new));
        }
        rabbitTemplate.setConfirmCallback(this.confirmCallback);
        rabbitTemplate.setReturnCallback(this.returnCallback);
        rabbitTemplate.setMandatory(this.mandatory);
    }


    protected abstract void initBinding();

    protected String buildRouteKey(String queueName) {
        return queueName + RabbitMQ.ROUTE_KEY_SUFFIX;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化绑定
        this.initBinding();
    }
}
