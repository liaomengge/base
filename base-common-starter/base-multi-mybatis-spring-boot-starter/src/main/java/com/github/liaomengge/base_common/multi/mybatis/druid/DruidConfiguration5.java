package com.github.liaomengge.base_common.multi.mybatis.druid;

import com.github.liaomengge.base_common.multi.mybatis.druid.properties.DruidStatProperties;
import com.github.liaomengge.base_common.multi.mybatis.druid.stat.DruidFilterConfiguration;
import com.github.liaomengge.base_common.multi.mybatis.druid.stat.DruidStatViewServletConfiguration;
import com.github.liaomengge.base_common.multi.mybatis.druid.stat.DruidWebStatFilterConfiguration;
import com.github.liaomengge.base_common.multi.mybatis.druid.wrapper.DruidDataSourceBuilder;

import com.alibaba.druid.pool.DruidDataSource;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2018/12/19.
 */
@ConditionalOnClass(DruidDataSource.class)
@EnableConfigurationProperties(DruidStatProperties.class)
@ConditionalOnProperty(name = "base.datasource.type", havingValue = "com.alibaba.druid.pool.DruidDataSource",
        matchIfMissing = true)
@Configuration
@Import({DruidStatViewServletConfiguration.class,
        DruidWebStatFilterConfiguration.class,
        DruidFilterConfiguration.class})
public class DruidConfiguration5 {

    @Bean(name = "fiveParentDataSource")
    @ConfigurationProperties("base.mybatis.five.druid")
    public DruidDataSource parentDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(value = "fiveMasterDataSource", initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("base.mybatis.five.druid.master")
    public DruidDataSource masterDataSource(@Qualifier("fiveParentDataSource") DruidDataSource dataSource) {
        if (Objects.isNull(dataSource)) {
            return DruidDataSourceBuilder.create().build();
        }
        return dataSource.cloneDruidDataSource();
    }

    @Bean(value = "fiveSlaveDataSource", initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("base.mybatis.five.druid.slave")
    public DruidDataSource slaveDataSource(@Qualifier("fiveParentDataSource") DruidDataSource dataSource) {
        if (Objects.isNull(dataSource)) {
            return DruidDataSourceBuilder.create().build();
        }
        return dataSource.cloneDruidDataSource();
    }
}
