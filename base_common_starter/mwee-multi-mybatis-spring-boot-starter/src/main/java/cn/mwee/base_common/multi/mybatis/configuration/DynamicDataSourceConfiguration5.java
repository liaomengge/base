package cn.mwee.base_common.multi.mybatis.configuration;

import cn.mwee.base_common.helper.mybatis.plugins.FlowInterceptor;
import cn.mwee.base_common.helper.mybatis.plugins.SqlInterceptor;
import cn.mwee.base_common.multi.mybatis.MybatisProperties;
import cn.mwee.base_common.multi.mybatis.MybatisProperties.TxProperties;
import cn.mwee.base_common.multi.mybatis.batch.BatchGeneralService;
import cn.mwee.base_common.multi.mybatis.druid.DruidConfiguration5;
import cn.mwee.base_common.multi.mybatis.extend.ExtendMapperScan;
import cn.mwee.base_common.multi.mybatis.extend.ExtendSpringBootVFS;
import cn.mwee.base_common.multi.mybatis.hikari.HikariConfiguration5;
import cn.mwee.base_common.support.datasource.DynamicDataSource;
import cn.mwee.base_common.support.datasource.enums.DbType;
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
@ConditionalOnProperty(prefix = "mwee.mybatis.five", name = "enable", havingValue = "true")
@Import({DruidConfiguration5.class, HikariConfiguration5.class})
@ExtendMapperScan(basePackages = "${mwee.mybatis.five.basePackages}", sqlSessionFactoryRef = "fiveSqlSessionFactory")
public class DynamicDataSourceConfiguration5 {

    @Bean("mybatisProperties5")
    @ConfigurationProperties(prefix = "mwee.mybatis.five")
    public MybatisProperties mybatisProperties5() {
        return new MybatisProperties();
    }

    @Bean("pageHelperProperties5")
    @ConfigurationProperties(prefix = "mwee.mybatis.five.pagehelper")
    public Properties pageHelperProperties5() {
        return new Properties();
    }

    @Bean("flowProperties5")
    @ConfigurationProperties(prefix = "mwee.mybatis.five.flow")
    public Properties flowProperties5() {
        return new Properties();
    }

    @RefreshScope
    @Bean("flowInterceptor5")
    public FlowInterceptor flowInterceptor5() {
        FlowInterceptor flowInterceptor = new FlowInterceptor();
        flowInterceptor.setProperties(flowProperties5());
        return flowInterceptor;
    }

    @RefreshScope
    @Bean("pageInterceptor5")
    public PageInterceptor pageInterceptor5() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        pageInterceptor.setProperties(pageHelperProperties5());
        return pageInterceptor;
    }

    @RefreshScope
    @Bean("sqlInterceptor5")
    public SqlInterceptor sqlInterceptor5() {
        SqlInterceptor sqlInterceptor = new SqlInterceptor();
        Properties properties = new Properties();
        properties.setProperty("isEnableSqlLog", String.valueOf(this.mybatisProperties5().getIsEnableSqlLog()));
        sqlInterceptor.setProperties(properties);
        return sqlInterceptor;
    }

    @Bean("fiveDynamicDataSource")
    @ConditionalOnBean(name = {"fiveMasterDataSource", "fiveSlaveDataSource"})
    public DynamicDataSource dataSource(@Qualifier("fiveMasterDataSource") DataSource masterDataSource,
                                        @Qualifier("fiveSlaveDataSource") DataSource slaveDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = Maps.newHashMap();
        dataSourceMap.put(DbType.MASTER, masterDataSource);
        dataSourceMap.put(DbType.SLAVE, slaveDataSource);
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        return dynamicDataSource;
    }

    @Bean("fiveSqlSessionFactory")
    @ConditionalOnBean(name = {"fiveDynamicDataSource"})
    @ConditionalOnMissingBean(name = "fiveSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("fiveDynamicDataSource") DynamicDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(ExtendSpringBootVFS.class);
        sqlSessionFactoryBean.setConfigLocation(this.mybatisProperties5().resolveConfigLocation());
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
        sqlSessionFactoryBean.setMapperLocations(this.mybatisProperties5().resolveMapperLocations());

        sqlSessionFactoryBean.setPlugins(new Interceptor[]{sqlInterceptor5(), pageInterceptor5(), flowInterceptor5()});

        return sqlSessionFactoryBean.getObject();
    }

    @Bean("fiveBatchGeneralService")
    @ConditionalOnBean(name = {"fiveSqlSessionFactory"})
    @ConditionalOnMissingBean(name = "fiveBatchGeneralService")
    public BatchGeneralService batchGeneralService(@Qualifier("fiveSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new BatchGeneralService(sqlSessionFactory);
    }

    @Bean("fiveTxManager")
    @ConditionalOnBean(name = {"fiveDynamicDataSource"})
    @ConditionalOnMissingBean(name = "fiveTxManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("fiveDynamicDataSource") DynamicDataSource dynamicDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dynamicDataSource);
        TxProperties txProperties = this.mybatisProperties5().getTx();
        if (txProperties.getTimeout() > 0) {
            dataSourceTransactionManager.setDefaultTimeout(txProperties.getTimeout());
        }
        return dataSourceTransactionManager;
    }

    @Bean("fiveTxTemplate")
    @ConditionalOnBean(name = {"fiveTxManager"})
    @ConditionalOnMissingBean(name = "fiveTxTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("fiveTxManager") DataSourceTransactionManager dataSourceTransactionManager) {
        return new TransactionTemplate(dataSourceTransactionManager);
    }
}
