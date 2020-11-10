package com.github.liaomengge.base_common.multi.shardingsphere.configuration;

import com.github.liaomengge.base_common.helper.mybatis.plugins.FlowInterceptor;
import com.github.liaomengge.base_common.helper.mybatis.plugins.SqlInterceptor;
import com.github.liaomengge.base_common.multi.shardingsphere.ShardingSphereProperties;
import com.github.liaomengge.base_common.multi.shardingsphere.batch.BatchGeneralService;
import com.github.liaomengge.base_common.multi.shardingsphere.extend.ExtendMapperScan;
import com.github.liaomengge.base_common.multi.shardingsphere.extend.ExtendSpringBootVFS;
import com.github.pagehelper.PageInterceptor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by liaomengge on 2019/9/12.
 */
@Configuration
@ConditionalOnProperty(prefix = "base.shardingsphere.five", name = "enabled", havingValue = "true")
@ExtendMapperScan(basePackages = "${base.shardingsphere.five.mybatis.basePackages}", sqlSessionFactoryRef =
        "fiveSqlSessionFactory")
public class ShardingDataSourceConfiguration5 extends AbstractShardingDataSourceConfiguration {

    private Environment environment;

    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    @Bean("shardingSphereProperties5")
    @ConfigurationProperties(prefix = "base.shardingsphere.five")
    public ShardingSphereProperties shardingSphereProperties5() {
        return new ShardingSphereProperties();
    }

    @Bean("pageHelperProperties5")
    @ConfigurationProperties(prefix = "base.shardingsphere.five.mybatis.pagehelper")
    public Properties pageHelperProperties5() {
        return new Properties();
    }

    @Bean("flowProperties5")
    @ConfigurationProperties(prefix = "base.shardingsphere.five.mybatis.flow")
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
        properties.setProperty("isEnableSqlLog",
                String.valueOf(this.shardingSphereProperties5().getMybatis().getIsEnableSqlLog()));
        sqlInterceptor.setProperties(properties);
        return sqlInterceptor;
    }

    @Bean("fiveMasterSlaveDataSource")
    public DataSource masterSlaveDataSource() throws SQLException {
        String prefix = "base.shardingsphere.five.datasource.";
        List<String> dataSourceNames = getDataSourceNames(environment, prefix);
        if (CollectionUtils.isEmpty(dataSourceNames)) {
            throw new ShardingSphereException("datasource couldn't null");
        }
        String masterDataSourceName = dataSourceNames.get(0);
        List<String> slaveDataSourceNames;
        if (dataSourceNames.size() == 1) {
            slaveDataSourceNames = dataSourceNames;
        } else {
            slaveDataSourceNames = dataSourceNames.subList(1, dataSourceNames.size());
        }
        MasterSlaveRuleConfiguration configuration = new MasterSlaveRuleConfiguration("fiveMasterSlaveRule",
                masterDataSourceName, slaveDataSourceNames);
        return MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, configuration,
                shardingSphereProperties5().getProps());
    }

    @Bean("fiveSqlSessionFactory")
    @ConditionalOnBean(name = {"fiveMasterSlaveDataSource"})
    @ConditionalOnMissingBean(name = "fiveSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("fiveMasterSlaveDataSource") DataSource dataSource) throws Exception {
        ShardingSphereProperties shardingSphereProperties = this.shardingSphereProperties5();
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(ExtendSpringBootVFS.class);
        sqlSessionFactoryBean.setConfigLocation(shardingSphereProperties.getMybatis().resolveConfigLocation());
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(shardingSphereProperties.getMybatis().resolveMapperLocations());

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
    @ConditionalOnBean(name = {"fiveMasterSlaveDataSource"})
    @ConditionalOnMissingBean(name = "fiveTxManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("fiveMasterSlaveDataSource") DataSource dataSource) {
        ShardingSphereProperties shardingSphereProperties = this.shardingSphereProperties5();
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        ShardingSphereProperties.MybatisProperties.TxProperties txProperties =
                shardingSphereProperties.getMybatis().getTx();
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

    @Override
    public final void setEnvironment(Environment environment) {
        this.environment = environment;
        String prefix = "base.shardingsphere.five.datasource.";
        for (String each : getDataSourceNames(environment, prefix)) {
            try {
                dataSourceMap.put(each, getDataSource(environment, prefix, each));
            } catch (ReflectiveOperationException ex) {
                throw new ShardingSphereException("Can't find five datasource type!", ex);
            } catch (NamingException namingEx) {
                throw new ShardingSphereException("Can't find JNDI five datasource!", namingEx);
            }
        }
    }
}
