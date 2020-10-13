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
import org.apache.shardingsphere.core.exception.ShardingException;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
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
@ConditionalOnProperty(prefix = "base.shardingsphere.four", name = "enabled", havingValue = "true")
@ExtendMapperScan(basePackages = "${base.shardingsphere.four.mybatis.basePackages}", sqlSessionFactoryRef =
        "fourSqlSessionFactory")
public class ShardingDataSourceConfiguration4 extends AbstractShardingDataSourceConfiguration {

    private Environment environment;

    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    @Bean("shardingSphereProperties4")
    @ConfigurationProperties(prefix = "base.shardingsphere.four")
    public ShardingSphereProperties shardingSphereProperties4() {
        return new ShardingSphereProperties();
    }

    @Bean("pageHelperProperties4")
    @ConfigurationProperties(prefix = "base.shardingsphere.four.mybatis.pagehelper")
    public Properties pageHelperProperties4() {
        return new Properties();
    }

    @Bean("flowProperties4")
    @ConfigurationProperties(prefix = "base.shardingsphere.four.mybatis.flow")
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
        properties.setProperty("isEnableSqlLog",
                String.valueOf(this.shardingSphereProperties4().getMybatis().getIsEnableSqlLog()));
        sqlInterceptor.setProperties(properties);
        return sqlInterceptor;
    }

    @Bean("fourMasterSlaveDataSource")
    public DataSource masterSlaveDataSource() throws SQLException {
        String prefix = "base.shardingsphere.four.datasource.";
        List<String> dataSourceNames = getDataSourceNames(environment, prefix);
        if (CollectionUtils.isEmpty(dataSourceNames)) {
            throw new ShardingException("datasource couldn't null");
        }
        String masterDataSourceName = dataSourceNames.get(0);
        List<String> slaveDataSourceNames;
        if (dataSourceNames.size() == 1) {
            slaveDataSourceNames = dataSourceNames;
        } else {
            slaveDataSourceNames = dataSourceNames.subList(1, dataSourceNames.size());
        }
        MasterSlaveRuleConfiguration configuration = new MasterSlaveRuleConfiguration("fourMasterSlaveRule",
                masterDataSourceName, slaveDataSourceNames);
        return MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, configuration,
                shardingSphereProperties4().getProps());
    }

    @Bean("fourSqlSessionFactory")
    @ConditionalOnBean(name = {"fourMasterSlaveDataSource"})
    @ConditionalOnMissingBean(name = "fourSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("fourMasterSlaveDataSource") DataSource dataSource) throws Exception {
        ShardingSphereProperties shardingSphereProperties = this.shardingSphereProperties4();
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(ExtendSpringBootVFS.class);
        sqlSessionFactoryBean.setConfigLocation(shardingSphereProperties.getMybatis().resolveConfigLocation());
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(shardingSphereProperties.getMybatis().resolveMapperLocations());

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
    @ConditionalOnBean(name = {"fourMasterSlaveDataSource"})
    @ConditionalOnMissingBean(name = "fourTxManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("fourMasterSlaveDataSource") DataSource dataSource) {
        ShardingSphereProperties shardingSphereProperties = this.shardingSphereProperties4();
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        ShardingSphereProperties.MybatisProperties.TxProperties txProperties =
                shardingSphereProperties.getMybatis().getTx();
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

    @Override
    public final void setEnvironment(Environment environment) {
        this.environment = environment;
        String prefix = "base.shardingsphere.four.datasource.";
        for (String each : getDataSourceNames(environment, prefix)) {
            try {
                dataSourceMap.put(each, getDataSource(environment, prefix, each));
            } catch (ReflectiveOperationException ex) {
                throw new ShardingException("Can't find four datasource type!", ex);
            } catch (NamingException namingEx) {
                throw new ShardingException("Can't find JNDI four datasource!", namingEx);
            }
        }
    }
}
