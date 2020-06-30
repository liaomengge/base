package cn.ly.base_common.mq.rabbitmq;

import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

/**
 * Created by liaomengge on 2019/5/5.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "mwee.mq.rabbitmq")
public class RabbitMQProperties {

    @NotNull
    private String virtualHost;
    @NotNull
    private String addresses;
    private String username;
    private String password;
    private Integer requestedHeartbeat;
    private Integer connectionTimeout;
    private boolean publisherConfirms;
    private boolean publisherReturns;
    private final Cache cache = new Cache();
    private final Retry retry = new Retry();
    private List<QueueProperties> queues = Lists.newArrayList();

    @Data
    public static class Cache {

        private final Channel channel = new Channel();

        private final Connection connection = new Connection();

        public Channel getChannel() {
            return this.channel;
        }

        public Connection getConnection() {
            return this.connection;
        }

        @Data
        public static class Channel {
            private Integer size;
            private Long checkoutTimeout;
        }

        @Data
        public static class Connection {
            private CachingConnectionFactory.CacheMode mode = CachingConnectionFactory.CacheMode.CHANNEL;
            private Integer size;
        }
    }

    @Data
    public static class Retry {
        private int maxAttempts = 3;
        private long initialInterval = 1000L;
        private double multiplier = 1.0;
        private long maxInterval = 10000L;
    }

    @Data
    @Validated
    public static class QueueProperties {
        private String beanName;
        private String exchangeName;
        @NotNull
        private String baseQueueName;
        private int queueCount = 1;

        public String buildBeanName() {
            if (StringUtils.isBlank(this.getBeanName())) {
                return LOWER_UNDERSCORE.to(LOWER_CAMEL, this.getBaseQueueName()) + "QueueConfig";
            }
            return this.getBeanName();
        }

        public String buildExchangeName() {
            if (StringUtils.isBlank(this.getExchangeName())) {
                return this.getBaseQueueName() + "_exchange";
            }
            return this.getExchangeName();
        }
    }
}
