package cn.ly.base_common.mq.rabbitmq.sender;

import cn.ly.base_common.helper.metric.rabbitmq.RabbitMQMonitor;
import cn.ly.base_common.mq.consts.MetricsConst;
import cn.ly.base_common.mq.rabbitmq.domain.QueueConfig;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.retry.support.RetryTemplate;

import lombok.NonNull;
import lombok.Setter;

/**
 * Created by liaomengge on 17/1/5.
 */
public class SimpleMQSender extends BaseMQSender {

    @Setter
    @NonNull
    private QueueConfig queueConfig;

    public SimpleMQSender(CachingConnectionFactory cachingConnectionFactory, RabbitAdmin rabbitAdmin,
                          RetryTemplate retryTemplate, RabbitTemplate.ConfirmCallback confirmCallback,
                          RabbitTemplate.ReturnCallback returnCallback, boolean mandatory,
                          RabbitMQMonitor rabbitMQMonitor) {
        super(cachingConnectionFactory, rabbitAdmin, retryTemplate, confirmCallback, returnCallback, mandatory,
                rabbitMQMonitor);
    }

    public SimpleMQSender(CachingConnectionFactory cachingConnectionFactory, RabbitAdmin rabbitAdmin,
                          MessageConverter messageConverter, List<MessagePostProcessor> messagePostProcessors,
                          RetryTemplate retryTemplate, RabbitTemplate.ConfirmCallback confirmCallback,
                          RabbitTemplate.ReturnCallback returnCallback, boolean mandatory,
                          RabbitMQMonitor rabbitMQMonitor) {
        super(cachingConnectionFactory, rabbitAdmin, messageConverter, messagePostProcessors, retryTemplate,
                confirmCallback, returnCallback, mandatory, rabbitMQMonitor);
    }

    @Override
    protected void initBinding() {
        if (StringUtils.isBlank(this.queueConfig.getExchangeName()) || ArrayUtils.isEmpty(this.queueConfig.buildQueueNames())) {
            log.error("目标队列为空/交换机为空/队列数为空, 无法初始化绑定, 请检查配置！");
            return;
        }
        DirectExchange directExchange = new DirectExchange(this.queueConfig.getExchangeName(), true, false);
        this.rabbitAdmin.declareExchange(directExchange);
        Queue queue;
        Binding binding;
        Map<String, Object> arguments = ImmutableMap.of("x-ha-policy", "all");
        for (String queueName : this.queueConfig.buildQueueNames()) {
            queue = new Queue(queueName, true, false, false, arguments);
            this.rabbitAdmin.declareQueue(queue);
            binding = BindingBuilder.bind(queue).to(directExchange).with(super.buildRouteKey(queueName));
            this.rabbitAdmin.declareBinding(binding);
        }
    }

    @Override
    public void convertAndSend(int routeKeyHash, Object object) {
        this.convertAndSend(queueConfig.buildRouteKey(routeKeyHash), object);
    }

    @Override
    public void convertAndSend(String routingKey, Object object) {
        this.convertAndSend(this.queueConfig.getExchangeName(), routingKey, object);
    }

    @Override
    public void convertAndSend(String exchangeName, String routingKey, Object object) {
        rabbitMQMonitor.monitorCount(MetricsConst.ENQUEUE_COUNT + "." + exchangeName);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, object);
    }

    @Override
    public void convertAndSend2(String queueName, Object object) {
        this.convertAndSend2(this.queueConfig.getExchangeName(), super.buildRouteKey(queueName), object);
    }

    @Override
    public void convertAndSend2(String exchangeName, String queueName, Object object) {
        this.convertAndSend(exchangeName, super.buildRouteKey(queueName), object);
    }
}
