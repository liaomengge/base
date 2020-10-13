package com.github.liaomengge.base_common.mybatis.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.liaomengge.base_common.mybatis.druid.properties.DruidStatProperties;
import com.github.liaomengge.base_common.mybatis.druid.stat.DruidFilterConfiguration;
import com.github.liaomengge.base_common.mybatis.druid.stat.DruidStatViewServletConfiguration;
import com.github.liaomengge.base_common.mybatis.druid.stat.DruidWebStatFilterConfiguration;
import com.github.liaomengge.base_common.mybatis.druid.wrapper.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Created by liaomengge on 2018/12/12.
 */
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnMissingBean(DataSource.class)
@EnableConfigurationProperties(DruidStatProperties.class)
@ConditionalOnProperty(name = "base.datasource.type", havingValue = "com.alibaba.druid.pool.DruidDataSource",
        matchIfMissing = true)
@Configuration
@Import({DruidStatViewServletConfiguration.class,
        DruidWebStatFilterConfiguration.class,
        DruidFilterConfiguration.class})
public class DruidConfiguration {

    @Bean(name = "parentDataSource")
    @ConfigurationProperties("base.mybatis.druid")
    public DruidDataSource parentDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "masterDataSource", initMethod = "init", destroyMethod = "close")
    @Primary
    @ConfigurationProperties("base.mybatis.druid.master")
    public DruidDataSource masterDataSource(@Qualifier("parentDataSource") DruidDataSource dataSource) {
        if (Objects.isNull(dataSource)) {
            return DruidDataSourceBuilder.create().build();
        }
        return dataSource.cloneDruidDataSource();
    }

    @Bean(name = "slaveDataSource", initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("base.mybatis.druid.slave")
    public DruidDataSource slaveDataSource(@Qualifier("parentDataSource") DruidDataSource dataSource) {
        if (Objects.isNull(dataSource)) {
            return DruidDataSourceBuilder.create().build();
        }
        return dataSource.cloneDruidDataSource();
    }
}
