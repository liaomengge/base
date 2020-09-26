package cn.ly.base_common.multi.shardingsphere.configuration;

import cn.ly.base_common.multi.shardingsphere.util.DataSourceUtil;
import cn.ly.base_common.multi.shardingsphere.util.PropertyUtil;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.shardingsphere.core.util.InlineExpressionParser;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jndi.JndiObjectFactoryBean;

/**
 * Created by liaomengge on 2019/9/12.
 */
public abstract class AbstractShardingDataSourceConfiguration implements EnvironmentAware {

    private final String jndiName = "jndi-name";

    protected List<String> getDataSourceNames(Environment environment, String prefix) {
        StandardEnvironment standardEnv = (StandardEnvironment) environment;
        standardEnv.setIgnoreUnresolvableNestedPlaceholders(true);
        return new InlineExpressionParser(standardEnv.getProperty(prefix + "names")).splitAndEvaluate();
    }

    protected DataSource getDataSource(Environment environment, String prefix, String dataSourceName) throws ReflectiveOperationException, NamingException {
        Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + dataSourceName.trim(),
                Map.class);
        Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
        if (dataSourceProps.containsKey(jndiName)) {
            return getJndiDataSource(dataSourceProps.get(jndiName).toString());
        }
        return DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
    }

    protected DataSource getJndiDataSource(String jndiName) throws NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setResourceRef(true);
        bean.setJndiName(jndiName);
        bean.setProxyInterface(DataSource.class);
        bean.afterPropertiesSet();
        return (DataSource) bean.getObject();
    }
}
