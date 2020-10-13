package com.github.liaomengge.base_common.multi.mybatis.configuration;

import com.github.liaomengge.base_common.helper.mybatis.plugins.FlowInterceptor;
import com.github.liaomengge.base_common.helper.mybatis.plugins.SqlInterceptor;
import com.github.liaomengge.base_common.multi.mybatis.MybatisProperties;
import com.github.liaomengge.base_common.multi.mybatis.batch.BatchGeneralService;
import com.github.liaomengge.base_common.multi.mybatis.druid.DruidConfiguration2;
import com.github.liaomengge.base_common.multi.mybatis.extend.ExtendMapperScan;
import com.github.liaomengge.base_common.multi.mybatis.extend.ExtendSpringBootVFS;
import com.github.liaomengge.base_common.multi.mybatis.hikari.HikariConfiguration2;
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
@Import({DruidConfiguration2.class, HikariConfiguration2.class})
@ExtendMapperScan(basePackages = "${base.mybatis.two.basePackages}", sqlSessionFactoryRef = "twoSqlSessionFactory")
public class DynamicDataSourceConfiguration2 {

    @Bean("mybatisProperties2")
    @ConfigurationProperties(prefix = "base.mybatis.two")
    public MybatisProperties mybatisProperties2() {
        return new MybatisProperties();
    }

    @Bean("pageHelperProperties2")
    @ConfigurationProperties(prefix = "base.mybatis.two.pagehelper")
    public Properties pageHelperProperties2() {
        return new Properties();
    }

    @Bean("flowProperties2")
    @ConfigurationProperties(prefix = "base.mybatis.two.flow")
    public Properties flowProperties2() {
        return new Properties();
    }

    @RefreshScope
    @Bean("flowInterceptor2")
    public FlowInterceptor flowInterceptor2() {
        FlowInterceptor flowInterceptor = new FlowInterceptor();
        flowInterceptor.setProperties(flowProperties2());
        return flowInterceptor;
    }

    @RefreshScope
    @Bean("pageInterceptor2")
    public PageInterceptor pageInterceptor2() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        pageInterceptor.setProperties(pageHelperProperties2());
        return pageInterceptor;
    }

    @RefreshScope
    @Bean("sqlInterceptor2")
    public SqlInterceptor sqlInterceptor2() {
        SqlInterceptor sqlInterceptor = new SqlInterceptor();
        Properties properties = new Properties();
        properties.setProperty("isEnableSqlLog", String.valueOf(this.mybatisProperties2().getIsEnableSqlLog()));
        sqlInterceptor.setProperties(properties);
        return sqlInterceptor;
    }

    @Bean("twoDynamicDataSource")
    @ConditionalOnBean(name = {"twoMasterDataSource", "twoSlaveDataSource"})
    public DynamicDataSource dataSource(@Qualifier("twoMasterDataSource") DataSource masterDataSource,
                                        @Qualifier("twoSlaveDataSource") DataSource slaveDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = Maps.newHashMap();
        dataSourceMap.put(DbType.MASTER, masterDataSource);
        dataSourceMap.put(DbType.SLAVE, slaveDataSource);
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        return dynamicDataSource;
    }

    @Bean("twoSqlSessionFactory")
    @ConditionalOnBean(name = {"twoDynamicDataSource"})
    @ConditionalOnMissingBean(name = "twoSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("twoDynamicDataSource") DynamicDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(ExtendSpringBootVFS.class);
        sqlSessionFactoryBean.setConfigLocation(this.mybatisProperties2().resolveConfigLocation());
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
        sqlSessionFactoryBean.setMapperLocations(this.mybatisProperties2().resolveMapperLocations());

        sqlSessionFactoryBean.setPlugins(new Interceptor[]{sqlInterceptor2(), pageInterceptor2(), flowInterceptor2()});

        return sqlSessionFactoryBean.getObject();
    }

    @Bean("twoBatchGeneralService")
    @ConditionalOnBean(name = {"twoSqlSessionFactory"})
    @ConditionalOnMissingBean(name = "twoBatchGeneralService")
    public BatchGeneralService batchGeneralService(@Qualifier("twoSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new BatchGeneralService(sqlSessionFactory);
    }

    @Bean("twoTxManager")
    @ConditionalOnBean(name = {"twoDynamicDataSource"})
    @ConditionalOnMissingBean(name = "twoTxManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("twoDynamicDataSource") DynamicDataSource dynamicDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dynamicDataSource);
        MybatisProperties.TxProperties txProperties = this.mybatisProperties2().getTx();
        if (txProperties.getTimeout() > 0) {
            dataSourceTransactionManager.setDefaultTimeout(txProperties.getTimeout());
        }
        return dataSourceTransactionManager;
    }

    @Bean("twoTxTemplate")
    @ConditionalOnBean(name = {"twoTxManager"})
    @ConditionalOnMissingBean(name = "twoTxTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("twoTxManager") DataSourceTransactionManager dataSourceTransactionManager) {
        return new TransactionTemplate(dataSourceTransactionManager);
    }
}
