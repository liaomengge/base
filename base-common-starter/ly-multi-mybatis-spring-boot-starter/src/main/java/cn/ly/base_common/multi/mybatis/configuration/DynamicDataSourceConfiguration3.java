package cn.ly.base_common.multi.mybatis.configuration;

import cn.ly.base_common.multi.mybatis.MybatisProperties;
import cn.ly.base_common.multi.mybatis.batch.BatchGeneralService;
import cn.ly.base_common.multi.mybatis.druid.DruidConfiguration3;
import cn.ly.base_common.multi.mybatis.extend.ExtendMapperScan;
import cn.ly.base_common.multi.mybatis.extend.ExtendSpringBootVFS;
import cn.ly.base_common.multi.mybatis.hikari.HikariConfiguration3;
import cn.ly.base_common.helper.mybatis.plugins.FlowInterceptor;
import cn.ly.base_common.helper.mybatis.plugins.SqlInterceptor;
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
@ConditionalOnProperty(prefix = "mwee.mybatis.three", name = "enable", havingValue = "true")
@Import({DruidConfiguration3.class, HikariConfiguration3.class})
@ExtendMapperScan(basePackages = "${mwee.mybatis.three.basePackages}", sqlSessionFactoryRef = "threeSqlSessionFactory")
public class DynamicDataSourceConfiguration3 {

    @Bean("mybatisProperties3")
    @ConfigurationProperties(prefix = "mwee.mybatis.three")
    public MybatisProperties mybatisProperties3() {
        return new MybatisProperties();
    }

    @Bean("pageHelperProperties3")
    @ConfigurationProperties(prefix = "mwee.mybatis.three.pagehelper")
    public Properties pageHelperProperties3() {
        return new Properties();
    }

    @Bean("flowProperties3")
    @ConfigurationProperties(prefix = "mwee.mybatis.three.flow")
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
        properties.setProperty("isEnableSqlLog", String.valueOf(this.mybatisProperties3().getIsEnableSqlLog()));
        sqlInterceptor.setProperties(properties);
        return sqlInterceptor;
    }

    @Bean("threeDynamicDataSource")
    @ConditionalOnBean(name = {"threeMasterDataSource", "threeSlaveDataSource"})
    public DynamicDataSource dataSource(@Qualifier("threeMasterDataSource") DataSource masterDataSource,
                                        @Qualifier("threeSlaveDataSource") DataSource slaveDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = Maps.newHashMap();
        dataSourceMap.put(DbType.MASTER, masterDataSource);
        dataSourceMap.put(DbType.SLAVE, slaveDataSource);
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        return dynamicDataSource;
    }

    @Bean("threeSqlSessionFactory")
    @ConditionalOnBean(name = {"threeDynamicDataSource"})
    @ConditionalOnMissingBean(name = "threeSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("threeDynamicDataSource") DynamicDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(ExtendSpringBootVFS.class);
        sqlSessionFactoryBean.setConfigLocation(this.mybatisProperties3().resolveConfigLocation());
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
        sqlSessionFactoryBean.setMapperLocations(this.mybatisProperties3().resolveMapperLocations());

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
    @ConditionalOnBean(name = {"threeDynamicDataSource"})
    @ConditionalOnMissingBean(name = "threeTxManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("threeDynamicDataSource") DynamicDataSource dynamicDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dynamicDataSource);
        MybatisProperties.TxProperties txProperties = this.mybatisProperties3().getTx();
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
}
