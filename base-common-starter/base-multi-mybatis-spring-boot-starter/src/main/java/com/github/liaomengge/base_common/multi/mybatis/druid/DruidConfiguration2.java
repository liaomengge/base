package com.github.liaomengge.base_common.multi.mybatis.druid;

import com.github.liaomengge.base_common.multi.mybatis.druid.stat.DruidFilterConfiguration;
import com.github.liaomengge.base_common.multi.mybatis.druid.stat.DruidStatViewServletConfiguration;
import com.github.liaomengge.base_common.multi.mybatis.druid.properties.DruidStatProperties;
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
public class DruidConfiguration2 {

    @Bean(name = "twoParentDataSource")
    @ConfigurationProperties("base.mybatis.two.druid")
    public DruidDataSource parentDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(value = "twoMasterDataSource", initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("base.mybatis.two.druid.master")
    public DruidDataSource masterDataSource(@Qualifier("twoParentDataSource") DruidDataSource dataSource) {
        if (Objects.isNull(dataSource)) {
            return DruidDataSourceBuilder.create().build();
        }
        return dataSource.cloneDruidDataSource();
    }

    @Bean(value = "twoSlaveDataSource", initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("base.mybatis.two.druid.slave")
    public DruidDataSource slaveDataSource(@Qualifier("twoParentDataSource") DruidDataSource dataSource) {
        if (Objects.isNull(dataSource)) {
            return DruidDataSourceBuilder.create().build();
        }
        return dataSource.cloneDruidDataSource();
    }
}
