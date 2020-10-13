package com.github.liaomengge.base_common.health_check.health;

import com.github.liaomengge.base_common.health_check.health.domain.HealthInfo;

import java.sql.Connection;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by liaomengge on 2019/7/11.
 */
public class DataSourceHealthCheck extends HealthCheck {

    @Override
    protected void doHealthCheck(HealthInfo healthInfo) throws Exception {
        Map<String, AbstractRoutingDataSource> dataSourceMap =
                super.applicationContext.getBeansOfType(AbstractRoutingDataSource.class);
        if (MapUtils.isNotEmpty(dataSourceMap)) {
            for (Map.Entry<String, AbstractRoutingDataSource> entry : dataSourceMap.entrySet()) {
                AbstractRoutingDataSource dataSource = entry.getValue();
                Connection connection = null;
                try {
                    connection = dataSource.getConnection();
                    String metaDate = connection.getMetaData().getDatabaseProductName();
                    healthInfo.withMetaData(metaDate);
                } finally {
                    if (Objects.nonNull(connection)) {
                        connection.close();
                    }
                }
            }
        }
    }
}
