package cn.ly.base_common.health_check.health;

import cn.ly.base_common.health_check.health.domain.HealthInfo;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

/**
 * Created by liaomengge on 2019/7/12.
 */
public class RabbitMQHealthCheck extends HealthCheck {

    @Override
    protected void doHealthCheck(HealthInfo healthInfo) throws Exception {
        Map<String, ConnectionFactory> connectionFactoryMap =
                super.applicationContext.getBeansOfType(ConnectionFactory.class);
        if (MapUtils.isNotEmpty(connectionFactoryMap)) {
            for (Map.Entry<String, ConnectionFactory> entry : connectionFactoryMap.entrySet()) {
                ConnectionFactory connectionFactory = entry.getValue();
                Connection connection = null;
                try {
                    connection = connectionFactory.createConnection();
                    healthInfo.withMetaData(connection.getLocalPort());
                } finally {
                    if (Objects.nonNull(connection)) {
                        connection.close();
                    }
                }
            }
        }
    }
}
