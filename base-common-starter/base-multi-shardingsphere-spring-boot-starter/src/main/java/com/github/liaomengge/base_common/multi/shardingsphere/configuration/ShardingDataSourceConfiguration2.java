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
@ConditionalOnProperty(prefix = "base.shardingsphere.two", name = "enabled", havingValue = "true")
@ExtendMapperScan(basePackages = "${base.shardingsphere.two.mybatis.basePackages}", sqlSessionFactoryRef =
        "twoSqlSessionFactory")
public class ShardingDataSourceConfiguration2 extends AbstractShardingDataSourceConfiguration {

    private Environment environment;

    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    @Bean("shardingSphereProperties2")
    @ConfigurationProperties("base.shardingsphere.two")
    public ShardingSphereProperties shardingSphereProperties2() {
        return new ShardingSphereProperties();
    }

    @Bean("pageHelperProperties2")
    @ConfigurationProperties("base.shardingsphere.two.mybatis.pagehelper")
    public Properties pageHelperProperties2() {
        return new Properties();
    }

    @Bean("flowProperties2")
    @ConfigurationProperties("base.shardingsphere.two.mybatis.flow")
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
        properties.setProperty("isEnableSqlLog",
                String.valueOf(this.shardingSphereProperties2().getMybatis().getIsEnableSqlLog()));
        sqlInterceptor.setProperties(properties);
        return sqlInterceptor;
    }

    @Bean("twoMasterSlaveDataSource")
    public DataSource masterSlaveDataSource() throws SQLException {
        String prefix = "base.shardingsphere.two.datasource.";
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
        MasterSlaveRuleConfiguration configuration = new MasterSlaveRuleConfiguration("twoMasterSlaveRule",
                masterDataSourceName, slaveDataSourceNames);
        return MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, configuration,
                shardingSphereProperties2().getProps());
    }

    @Bean("twoSqlSessionFactory")
    @ConditionalOnBean(name = {"twoMasterSlaveDataSource"})
    @ConditionalOnMissingBean(name = "twoSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("twoMasterSlaveDataSource") DataSource dataSource) throws Exception {
        ShardingSphereProperties shardingSphereProperties = this.shardingSphereProperties2();
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(ExtendSpringBootVFS.class);
        sqlSessionFactoryBean.setConfigLocation(shardingSphereProperties.getMybatis().resolveConfigLocation());
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(shardingSphereProperties.getMybatis().resolveMapperLocations());

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
    @ConditionalOnBean(name = {"twoMasterSlaveDataSource"})
    @ConditionalOnMissingBean(name = "twoTxManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("twoMasterSlaveDataSource") DataSource dataSource) {
        ShardingSphereProperties shardingSphereProperties = this.shardingSphereProperties2();
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        ShardingSphereProperties.MybatisProperties.TxProperties txProperties =
                shardingSphereProperties.getMybatis().getTx();
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

    @Override
    public final void setEnvironment(Environment environment) {
        this.environment = environment;
        String prefix = "base.shardingsphere.two.datasource.";
        for (String each : getDataSourceNames(environment, prefix)) {
            try {
                dataSourceMap.put(each, getDataSource(environment, prefix, each));
            } catch (ReflectiveOperationException ex) {
                throw new ShardingSphereException("Can't find two datasource type!", ex);
            } catch (NamingException namingEx) {
                throw new ShardingSphereException("Can't find JNDI two datasource!", namingEx);
            }
        }
    }
}
