package com.github.liaomengge.base_common.mybatis;

import com.github.liaomengge.base_common.helper.aspect.MasterSlaveAspect;
import com.github.liaomengge.base_common.helper.mybatis.plugins.FlowInterceptor;
import com.github.liaomengge.base_common.helper.mybatis.plugins.SqlInterceptor;
import com.github.liaomengge.base_common.helper.mybatis.transaction.callback.TransactionCallbackHelper;
import com.github.liaomengge.base_common.mybatis.batch.BatchGeneralService;
import com.github.liaomengge.base_common.mybatis.druid.DruidConfiguration;
import com.github.liaomengge.base_common.mybatis.extend.ExtendMapperScan;
import com.github.liaomengge.base_common.mybatis.extend.ExtendSpringBootVFS;
import com.github.liaomengge.base_common.mybatis.hikari.HikariConfiguration;
import com.github.liaomengge.base_common.support.datasource.DynamicDataSource;
import com.github.liaomengge.base_common.support.datasource.enums.DbType;
import com.github.pagehelper.PageInterceptor;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Map;
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
@Import({DruidConfiguration.class, HikariConfiguration.class})
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
    @ConditionalOnBean(name = {"masterDataSource", "slaveDataSource"})
    public DynamicDataSource dataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                        @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = Maps.newHashMap();
        dataSourceMap.put(DbType.MASTER, masterDataSource);
        dataSourceMap.put(DbType.SLAVE, slaveDataSource);
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        return dynamicDataSource;
    }

    @Bean
    @Primary
    @ConditionalOnBean(DynamicDataSource.class)
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactoryBean(DynamicDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(ExtendSpringBootVFS.class);
        sqlSessionFactoryBean.setConfigLocation(this.mybatisProperties.resolveConfigLocation());
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
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
    @ConditionalOnBean(DynamicDataSource.class)
    @ConditionalOnMissingBean
    public DataSourceTransactionManager dataSourceTransactionManager(DynamicDataSource dynamicDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dynamicDataSource);
        MybatisProperties.TxProperties txProperties = this.mybatisProperties.getTx();
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
    public MasterSlaveAspect masterSlaveAspect() {
        return new MasterSlaveAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionCallbackHelper transactionCallbackHelper() {
        return new TransactionCallbackHelper();
    }
}
