package com.github.liaomengge.base_common.multi.mybatis;

import com.github.liaomengge.base_common.helper.aspect.MasterSlaveAspect;
import com.github.liaomengge.base_common.helper.mybatis.transaction.callback.TransactionCallbackHelper;
import com.github.liaomengge.base_common.multi.mybatis.configuration.*;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Created by liaomengge on 2018/10/23.
 */
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@ConditionalOnClass(SqlSessionFactoryBean.class)
@EnableTransactionManagement(proxyTargetClass = true)
@Import({DynamicDataSourceConfiguration.class, DynamicDataSourceConfiguration2.class,
        DynamicDataSourceConfiguration3.class, DynamicDataSourceConfiguration4.class,
        DynamicDataSourceConfiguration5.class})
public class MybatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MasterSlaveAspect masterSlaveAspect() {
        return new MasterSlaveAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionCallbackHelper transactionCallbackHelper() {
        return new TransactionCallbackHelper();
    }
}
