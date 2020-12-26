package com.github.liaomengge.base_common.multi.mybatis.configuration;

import com.github.liaomengge.base_common.helper.mybatis.plugins.FlowInterceptor;
import com.github.liaomengge.base_common.helper.mybatis.plugins.SqlInterceptor;
import com.github.liaomengge.base_common.multi.mybatis.MybatisProperties;
import com.github.liaomengge.base_common.multi.mybatis.batch.BatchGeneralService;
import com.github.liaomengge.base_common.multi.mybatis.druid.DruidConfiguration;
import com.github.liaomengge.base_common.multi.mybatis.extend.ExtendMapperScan;
import com.github.liaomengge.base_common.multi.mybatis.extend.ExtendSpringBootVFS;
import com.github.liaomengge.base_common.multi.mybatis.hikari.HikariConfiguration;
import com.github.liaomengge.base_common.support.datasource.DynamicDataSource;
import com.github.liaomengge.base_common.support.datasource.enums.DbType;
import com.github.pagehelper.PageInterceptor;
import com.google.common.collect.Maps;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * Created by liaomengge on 2018/12/19.
 */
@Configuration
@Import({DruidConfiguration.class, HikariConfiguration.class})
@ExtendMapperScan(basePackages = "${base.mybatis.one.basePackages}", sqlSessionFactoryRef = "oneSqlSessionFactory")
public class DynamicDataSourceConfiguration {

    @Bean("mybatisProperties")
    @ConfigurationProperties("base.mybatis.one")
    public MybatisProperties mybatisProperties() {
        return new MybatisProperties();
    }

    @Bean("pageHelperProperties")
    @ConfigurationProperties("base.mybatis.one.pagehelper")
    public Properties pageHelperProperties() {
        return new Properties();
    }

    @Bean("flowProperties")
    @ConfigurationProperties("base.mybatis.one.flow")
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
        properties.setProperty("isEnableSqlLog", String.valueOf(this.mybatisProperties().getIsEnableSqlLog()));
        sqlInterceptor.setProperties(properties);
        return sqlInterceptor;
    }

    @Bean("oneDynamicDataSource")
    @ConditionalOnBean(name = {"oneMasterDataSource", "oneSlaveDataSource"})
    public DynamicDataSource dataSource(@Qualifier("oneMasterDataSource") DataSource masterDataSource,
                                        @Qualifier("oneSlaveDataSource") DataSource slaveDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = Maps.newHashMap();
        dataSourceMap.put(DbType.MASTER, masterDataSource);
        dataSourceMap.put(DbType.SLAVE, slaveDataSource);
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        return dynamicDataSource;
    }

    @Bean("oneSqlSessionFactory")
    @ConditionalOnBean(name = {"oneDynamicDataSource"})
    @ConditionalOnMissingBean(name = "oneSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("oneDynamicDataSource") DynamicDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(ExtendSpringBootVFS.class);
        sqlSessionFactoryBean.setConfigLocation(this.mybatisProperties().resolveConfigLocation());
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
        sqlSessionFactoryBean.setMapperLocations(this.mybatisProperties().resolveMapperLocations());

        sqlSessionFactoryBean.setPlugins(new Interceptor[]{sqlInterceptor(), pageInterceptor(), flowInterceptor()});

        return sqlSessionFactoryBean.getObject();
    }

    @Bean("oneBatchGeneralService")
    @ConditionalOnBean(name = {"oneSqlSessionFactory"})
    @ConditionalOnMissingBean(name = "oneBatchGeneralService")
    public BatchGeneralService batchGeneralService(@Qualifier("oneSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new BatchGeneralService(sqlSessionFactory);
    }

    @Bean("oneTxManager")
    @ConditionalOnBean(name = {"oneDynamicDataSource"})
    @ConditionalOnMissingBean(name = "oneTxManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("oneDynamicDataSource") DynamicDataSource dynamicDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dynamicDataSource);
        MybatisProperties.TxProperties txProperties = this.mybatisProperties().getTx();
        if (txProperties.getTimeout() > 0) {
            dataSourceTransactionManager.setDefaultTimeout(txProperties.getTimeout());
        }
        return dataSourceTransactionManager;
    }

    @Bean("oneTxTemplate")
    @ConditionalOnBean(name = {"oneTxManager"})
    @ConditionalOnMissingBean(name = "oneTxTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("oneTxManager") DataSourceTransactionManager dataSourceTransactionManager) {
        return new TransactionTemplate(dataSourceTransactionManager);
    }
}
