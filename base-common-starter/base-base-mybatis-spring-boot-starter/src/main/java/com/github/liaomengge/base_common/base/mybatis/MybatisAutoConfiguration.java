package com.github.liaomengge.base_common.base.mybatis;

import com.github.liaomengge.base_common.base.mybatis.MybatisProperties.TxProperties;
import com.github.liaomengge.base_common.base.mybatis.batch.BatchGeneralService;
import com.github.liaomengge.base_common.base.mybatis.druid.DruidConfiguration;
import com.github.liaomengge.base_common.base.mybatis.extend.ExtendMapperScan;
import com.github.liaomengge.base_common.base.mybatis.extend.ExtendSpringBootVFS;
import com.github.liaomengge.base_common.base.mybatis.hikari.HikariConfiguration;
import com.github.liaomengge.base_common.base.mybatis.registry.MybatisPointcutBeanRegistryConfiguration;
import com.github.liaomengge.base_common.helper.mybatis.plugins.FlowInterceptor;
import com.github.liaomengge.base_common.helper.mybatis.plugins.SqlInterceptor;
import com.github.liaomengge.base_common.helper.mybatis.transaction.callback.TransactionCallbackHelper;
import com.github.pagehelper.PageInterceptor;
import lombok.AllArgsConstructor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Properties;


/**
 * Created by liaomengge on 2018/10/23.
 */
@AllArgsConstructor
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@ConditionalOnClass(SqlSessionFactoryBean.class)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableConfigurationProperties(MybatisProperties.class)
@Import({DruidConfiguration.class, HikariConfiguration.class, MybatisPointcutBeanRegistryConfiguration.class})
@ExtendMapperScan("${base.mybatis.basePackages}")
public class MybatisAutoConfiguration {

    private final MybatisProperties mybatisProperties;

    @Bean("pageHelperProperties")
    @ConfigurationProperties("base.mybatis.pagehelper")
    public Properties pageHelperProperties() {
        return new Properties();
    }

    @Bean("flowProperties")
    @ConfigurationProperties("base.mybatis.flow")
    public Properties flowProperties() {
        return new Properties();
    }

    @RefreshScope
    @Bean("flowInterceptor")
    public FlowInterceptor flowInterceptor() {
        FlowInterceptor flowInterceptor = new FlowInterceptor();
        flowInterceptor.setProperties(flowProperties());
        return flowInterceptor;
    }

    @RefreshScope
    @Bean("pageInterceptor")
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        pageInterceptor.setProperties(pageHelperProperties());
        return pageInterceptor;
    }

    @RefreshScope
    @Bean("sqlInterceptor")
    public SqlInterceptor sqlInterceptor() {
        SqlInterceptor sqlInterceptor = new SqlInterceptor();
        Properties properties = new Properties();
        properties.setProperty("isEnableSqlLog", String.valueOf(this.mybatisProperties.getIsEnableSqlLog()));
        sqlInterceptor.setProperties(properties);
        return sqlInterceptor;
    }

    @Bean
    @Primary
    @ConditionalOnBean(AbstractRoutingDataSource.class)
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactoryBean(AbstractRoutingDataSource routingDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(ExtendSpringBootVFS.class);
        sqlSessionFactoryBean.setConfigLocation(this.mybatisProperties.resolveConfigLocation());
        sqlSessionFactoryBean.setDataSource(routingDataSource);
        sqlSessionFactoryBean.setMapperLocations(this.mybatisProperties.resolveMapperLocations());

        sqlSessionFactoryBean.setPlugins(new Interceptor[]{sqlInterceptor(), pageInterceptor(), flowInterceptor()});

        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    @ConditionalOnBean(SqlSessionFactory.class)
    @ConditionalOnMissingBean
    public BatchGeneralService batchGeneralService(SqlSessionFactory sqlSessionFactory) {
        return new BatchGeneralService(sqlSessionFactory);
    }

    @Bean
    @ConditionalOnClass(PlatformTransactionManager.class)
    @ConditionalOnBean(AbstractRoutingDataSource.class)
    @ConditionalOnMissingBean
    public DataSourceTransactionManager dataSourceTransactionManager(AbstractRoutingDataSource routingDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(routingDataSource);
        TxProperties txProperties = this.mybatisProperties.getTx();
        if (txProperties.getTimeout() > 0) {
            dataSourceTransactionManager.setDefaultTimeout(txProperties.getTimeout());
        }
        return dataSourceTransactionManager;
    }

    @Bean
    @ConditionalOnBean(PlatformTransactionManager.class)
    @ConditionalOnMissingBean
    public TransactionTemplate transactionTemplate(DataSourceTransactionManager dataSourceTransactionManager) {
        return new TransactionTemplate(dataSourceTransactionManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionCallbackHelper transactionCallbackHelper() {
        return new TransactionCallbackHelper();
    }
}
