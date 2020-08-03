package cn.ly.base_common.multi.mybatis.configuration;

import cn.ly.base_common.helper.mybatis.plugins.FlowInterceptor;
import cn.ly.base_common.helper.mybatis.plugins.SqlInterceptor;
import cn.ly.base_common.multi.mybatis.MybatisProperties;
import cn.ly.base_common.multi.mybatis.batch.BatchGeneralService;
import cn.ly.base_common.multi.mybatis.druid.DruidConfiguration4;
import cn.ly.base_common.multi.mybatis.extend.ExtendMapperScan;
import cn.ly.base_common.multi.mybatis.extend.ExtendSpringBootVFS;
import cn.ly.base_common.multi.mybatis.hikari.HikariConfiguration4;
import cn.ly.base_common.support.datasource.DynamicDataSource;
import cn.ly.base_common.support.datasource.enums.DbType;
import com.github.pagehelper.PageInterceptor;
import com.google.common.collect.Maps;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(prefix = "ly.mybatis.four", name = "enabled", havingValue = "true")
@Import({DruidConfiguration4.class, HikariConfiguration4.class})
@ExtendMapperScan(basePackages = "${ly.mybatis.four.basePackages}", sqlSessionFactoryRef = "fourSqlSessionFactory")
public class DynamicDataSourceConfiguration4 {

    @Bean("mybatisProperties4")
    @ConfigurationProperties(prefix = "ly.mybatis.four")
    public MybatisProperties mybatisProperties4() {
        return new MybatisProperties();
    }

    @Bean("pageHelperProperties4")
    @ConfigurationProperties(prefix = "ly.mybatis.four.pagehelper")
    public Properties pageHelperProperties4() {
        return new Properties();
    }

    @Bean("flowProperties4")
    @ConfigurationProperties(prefix = "ly.mybatis.four.flow")
    public Properties flowProperties4() {
        return new Properties();
    }

    @RefreshScope
    @Bean("flowInterceptor4")
    public FlowInterceptor flowInterceptor4() {
        FlowInterceptor flowInterceptor = new FlowInterceptor();
        flowInterceptor.setProperties(flowProperties4());
        return flowInterceptor;
    }

    @RefreshScope
    @Bean("pageInterceptor4")
    public PageInterceptor pageInterceptor4() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        pageInterceptor.setProperties(pageHelperProperties4());
        return pageInterceptor;
    }

    @RefreshScope
    @Bean("sqlInterceptor4")
    public SqlInterceptor sqlInterceptor4() {
        SqlInterceptor sqlInterceptor = new SqlInterceptor();
        Properties properties = new Properties();
        properties.setProperty("isEnableSqlLog", String.valueOf(this.mybatisProperties4().getIsEnableSqlLog()));
        sqlInterceptor.setProperties(properties);
        return sqlInterceptor;
    }

    @Bean("fourDynamicDataSource")
    @ConditionalOnBean(name = {"fourMasterDataSource", "fourSlaveDataSource"})
    public DynamicDataSource dataSource(@Qualifier("fourMasterDataSource") DataSource masterDataSource,
                                        @Qualifier("fourSlaveDataSource") DataSource slaveDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = Maps.newHashMap();
        dataSourceMap.put(DbType.MASTER, masterDataSource);
        dataSourceMap.put(DbType.SLAVE, slaveDataSource);
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        return dynamicDataSource;
    }

    @Bean("fourSqlSessionFactory")
    @ConditionalOnBean(name = {"fourDynamicDataSource"})
    @ConditionalOnMissingBean(name = "fourSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("fourDynamicDataSource") DynamicDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(ExtendSpringBootVFS.class);
        sqlSessionFactoryBean.setConfigLocation(this.mybatisProperties4().resolveConfigLocation());
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
        sqlSessionFactoryBean.setMapperLocations(this.mybatisProperties4().resolveMapperLocations());

        sqlSessionFactoryBean.setPlugins(new Interceptor[]{sqlInterceptor4(), pageInterceptor4(), flowInterceptor4()});

        return sqlSessionFactoryBean.getObject();
    }

    @Bean("fourBatchGeneralService")
    @ConditionalOnBean(name = {"fourSqlSessionFactory"})
    @ConditionalOnMissingBean(name = "fourBatchGeneralService")
    public BatchGeneralService batchGeneralService(@Qualifier("fourSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new BatchGeneralService(sqlSessionFactory);
    }

    @Bean("fourTxManager")
    @ConditionalOnBean(name = {"fourDynamicDataSource"})
    @ConditionalOnMissingBean(name = "fourTxManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("fourDynamicDataSource") DynamicDataSource dynamicDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dynamicDataSource);
        MybatisProperties.TxProperties txProperties = this.mybatisProperties4().getTx();
        if (txProperties.getTimeout() > 0) {
            dataSourceTransactionManager.setDefaultTimeout(txProperties.getTimeout());
        }
        return dataSourceTransactionManager;
    }

    @Bean("fourTxTemplate")
    @ConditionalOnBean(name = {"fourTxManager"})
    @ConditionalOnMissingBean(name = "fourTxTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("fourTxManager") DataSourceTransactionManager dataSourceTransactionManager) {
        return new TransactionTemplate(dataSourceTransactionManager);
    }
}
