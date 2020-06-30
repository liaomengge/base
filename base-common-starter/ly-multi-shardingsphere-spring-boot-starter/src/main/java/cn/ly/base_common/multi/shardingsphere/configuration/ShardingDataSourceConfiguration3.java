package cn.ly.base_common.multi.shardingsphere.configuration;

import cn.ly.base_common.multi.shardingsphere.ShardingSphereProperties;
import cn.ly.base_common.multi.shardingsphere.batch.BatchGeneralService;
import cn.ly.base_common.helper.mybatis.plugins.FlowInterceptor;
import cn.ly.base_common.helper.mybatis.plugins.SqlInterceptor;
import cn.ly.base_common.multi.shardingsphere.extend.ExtendMapperScan;
import cn.ly.base_common.multi.shardingsphere.extend.ExtendSpringBootVFS;
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
@ConditionalOnProperty(prefix = "ly.shardingsphere.three", name = "enable", havingValue = "true")
@ExtendMapperScan(basePackages = "${ly.shardingsphere.three.mybatis.basePackages}", sqlSessionFactoryRef =
        "threeSqlSessionFactory")
public class ShardingDataSourceConfiguration3 extends AbstractShardingDataSourceConfiguration {

    private Environment environment;

    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    @Bean("shardingSphereProperties3")
    @ConfigurationProperties(prefix = "ly.shardingsphere.three")
    public ShardingSphereProperties shardingSphereProperties3() {
        return new ShardingSphereProperties();
    }

    @Bean("pageHelperProperties3")
    @ConfigurationProperties(prefix = "ly.shardingsphere.three.mybatis.pagehelper")
    public Properties pageHelperProperties3() {
        return new Properties();
    }

    @Bean("flowProperties3")
    @ConfigurationProperties(prefix = "ly.shardingsphere.three.mybatis.flow")
    public Properties flowProperties3() {
        return new Properties();
    }

    @RefreshScope
    @Bean("flowInterceptor3")
    public FlowInterceptor flowInterceptor3() {
        FlowInterceptor flowInterceptor = new FlowInterceptor();
        flowInterceptor.setProperties(flowProperties3());
        return flowInterceptor;
    }

    @RefreshScope
    @Bean("pageInterceptor3")
    public PageInterceptor pageInterceptor3() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        pageInterceptor.setProperties(pageHelperProperties3());
        return pageInterceptor;
    }

    @RefreshScope
    @Bean("sqlInterceptor3")
    public SqlInterceptor sqlInterceptor3() {
        SqlInterceptor sqlInterceptor = new SqlInterceptor();
        Properties properties = new Properties();
        properties.setProperty("isEnableSqlLog",
                String.valueOf(this.shardingSphereProperties3().getMybatis().getIsEnableSqlLog()));
        sqlInterceptor.setProperties(properties);
        return sqlInterceptor;
    }

    @Bean("threeMasterSlaveDataSource")
    public DataSource masterSlaveDataSource() throws SQLException {
        String prefix = "ly.shardingsphere.three.datasource.";
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
        MasterSlaveRuleConfiguration configuration = new MasterSlaveRuleConfiguration("threeMasterSlaveRule",
                masterDataSourceName, slaveDataSourceNames);
        return MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, configuration,
                shardingSphereProperties3().getProps());
    }

    @Bean("threeSqlSessionFactory")
    @ConditionalOnBean(name = {"threeMasterSlaveDataSource"})
    @ConditionalOnMissingBean(name = "threeSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("threeMasterSlaveDataSource") DataSource dataSource) throws Exception {
        ShardingSphereProperties shardingSphereProperties = this.shardingSphereProperties3();
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(ExtendSpringBootVFS.class);
        sqlSessionFactoryBean.setConfigLocation(shardingSphereProperties.getMybatis().resolveConfigLocation());
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(shardingSphereProperties.getMybatis().resolveMapperLocations());

        sqlSessionFactoryBean.setPlugins(new Interceptor[]{sqlInterceptor3(), pageInterceptor3(), flowInterceptor3()});

        return sqlSessionFactoryBean.getObject();
    }

    @Bean("threeBatchGeneralService")
    @ConditionalOnBean(name = {"threeSqlSessionFactory"})
    @ConditionalOnMissingBean(name = "threeBatchGeneralService")
    public BatchGeneralService batchGeneralService(@Qualifier("threeSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new BatchGeneralService(sqlSessionFactory);
    }

    @Bean("threeTxManager")
    @ConditionalOnBean(name = {"threeMasterSlaveDataSource"})
    @ConditionalOnMissingBean(name = "threeTxManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("threeMasterSlaveDataSource") DataSource dataSource) {
        ShardingSphereProperties shardingSphereProperties = this.shardingSphereProperties3();
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        ShardingSphereProperties.MybatisProperties.TxProperties txProperties = shardingSphereProperties.getMybatis().getTx();
        if (txProperties.getTimeout() > 0) {
            dataSourceTransactionManager.setDefaultTimeout(txProperties.getTimeout());
        }
        return dataSourceTransactionManager;
    }

    @Bean("threeTxTemplate")
    @ConditionalOnBean(name = {"threeTxManager"})
    @ConditionalOnMissingBean(name = "threeTxTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("threeTxManager") DataSourceTransactionManager dataSourceTransactionManager) {
        return new TransactionTemplate(dataSourceTransactionManager);
    }

    @Override
    public final void setEnvironment(Environment environment) {
        this.environment = environment;
        String prefix = "ly.shardingsphere.three.datasource.";
        for (String each : getDataSourceNames(environment, prefix)) {
            try {
                dataSourceMap.put(each, getDataSource(environment, prefix, each));
            } catch (ReflectiveOperationException ex) {
                throw new ShardingException("Can't find three datasource type!", ex);
            } catch (NamingException namingEx) {
                throw new ShardingException("Can't find JNDI three datasource!", namingEx);
            }
        }
    }
}
