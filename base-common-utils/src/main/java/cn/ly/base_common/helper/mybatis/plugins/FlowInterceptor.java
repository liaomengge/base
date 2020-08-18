package cn.ly.base_common.helper.mybatis.plugins;

import cn.ly.base_common.utils.date.LyDateUtil;
import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import cn.ly.base_common.utils.json.LyJsonUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.properties.LyPropertiesUtil;
import cn.ly.base_common.utils.string.LyStringUtil;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by liaomengge on 2019/9/20.
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})}
)
public class FlowInterceptor implements Interceptor {

    private static final Logger log = LyLogger.getInstance(FlowInterceptor.class);

    private Boolean isEnableFailFast = Boolean.FALSE;
    private List<FlowConfig> flowConfigs = Lists.newArrayList();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (BooleanUtils.isTrue(isEnableFailFast) && CollectionUtils.isNotEmpty(flowConfigs)) {
            Object[] args = invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement) args[0];
            Configuration configuration = mappedStatement.getConfiguration();
            String sqlId = mappedStatement.getId();
            Object parameter = args[1];
            BoundSql boundSql;
            //由于逻辑关系, 只会进入一次
            if (args.length == 4) {
                //4 个参数时
                boundSql = mappedStatement.getBoundSql(parameter);
            } else {
                //6 个参数时
                boundSql = (BoundSql) args[5];
            }
            for (FlowConfig flowConfig : flowConfigs) {
                if (StringUtils.equals(sqlId, flowConfig.getSqlId())) {
                    boolean isFind = true;
                    List<KeyValue> keyValues = flowConfig.getKeyValues();
                    if (CollectionUtils.isNotEmpty(keyValues)) {
                        for (KeyValue keyValue : keyValues) {
                            isFind &= isParameterMatch(configuration, boundSql, keyValue);
                            if (!isFind) {
                                break;
                            }
                        }
                        if (isFind) {
                            log.warn("SQL[{}], Access Forbidden!!!", beautifySql(boundSql.getSql()));
                            throw new SQLException("Access Forbidden!!!");
                        }
                    }
                }
            }
        }
        return invocation.proceed();
    }

    private String beautifySql(String sql) {
        return sql.replaceAll("[\\s\n ]+", " ");
    }

    private boolean isParameterMatch(Configuration configuration, BoundSql boundSql, KeyValue keyValue) {
        Object parameterObject = boundSql.getParameterObject();
        if (Objects.nonNull(parameterObject)) {
            try {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                Object propertyValue = null;
                String key = keyValue.getKey();
                if (metaObject.hasGetter(key)) {
                    propertyValue = metaObject.getValue(key);
                } else if (boundSql.hasAdditionalParameter(key)) {
                    propertyValue = boundSql.getAdditionalParameter(key);
                }
                return StringUtils.equals(resolveParameter(propertyValue), keyValue.getValue());
            } catch (Exception e) {
                log.error("judge parameter match exception", e);
                return false;
            }
        }
        return false;
    }

    private String resolveParameter(Object parameterObject) {
        String result;
        if (parameterObject instanceof String) {
            result = parameterObject.toString();
        } else if (parameterObject instanceof Date) {
            result = LyDateUtil.getDate2String((Date) parameterObject);
        } else if (parameterObject instanceof LocalDateTime) {
            result = LyJdk8DateUtil.getDate2String((LocalDateTime) parameterObject);
        } else if (parameterObject instanceof LocalDate) {
            result = LyJdk8DateUtil.getDate2String((LocalDate) parameterObject);
        } else if (parameterObject instanceof LocalTime) {
            result = LyJdk8DateUtil.getDate2String((LocalTime) parameterObject);
        } else {
            result = LyStringUtil.getValue(parameterObject);
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        isEnableFailFast = LyPropertiesUtil.getBooleanProperty(properties, "isEnableFailFast", Boolean.FALSE);
        String config = LyPropertiesUtil.getStringProperty(properties, "flowConfigs");
        if (StringUtils.isNotBlank(config)) {
            try {
                flowConfigs = LyJsonUtil.fromJson(config, new TypeReference<List<FlowConfig>>() {
                });
            } catch (Exception e) {
                log.error("parse sql flow control config error", e);
                flowConfigs = Lists.newArrayList();
            }
        }
    }

    @Data
    private static class FlowConfig {
        private String sqlId;
        private List<KeyValue> keyValues;
    }

    @Data
    private static class KeyValue {
        private String key;
        private String value;
    }
}
