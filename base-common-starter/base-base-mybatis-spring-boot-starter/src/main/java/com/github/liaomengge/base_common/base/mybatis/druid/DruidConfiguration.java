package com.github.liaomengge.base_common.base.mybatis.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.liaomengge.base_common.base.mybatis.druid.properties.DruidStatProperties;
import com.github.liaomengge.base_common.base.mybatis.druid.stat.DruidFilterConfiguration;
import com.github.liaomengge.base_common.base.mybatis.druid.stat.DruidStatViewServletConfiguration;
import com.github.liaomengge.base_common.base.mybatis.druid.stat.DruidWebStatFilterConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2018/12/12.
 */
@ConditionalOnClass(DruidDataSource.class)
@EnableConfigurationProperties(DruidStatProperties.class)
@ConditionalOnProperty(name = "base.datasource.type", havingValue = "com.alibaba.druid.pool.DruidDataSource",
        matchIfMissing = true)
@Configuration
@Import({DruidStatViewServletConfiguration.class,
        DruidWebStatFilterConfiguration.class,
        DruidFilterConfiguration.class})
public class DruidConfiguration {
}
