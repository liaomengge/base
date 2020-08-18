package cn.ly.base_common.helper.mybatis.plugins;

import cn.ly.base_common.utils.date.LyDateUtil;
import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.number.LyMoreNumberUtil;
import cn.ly.base_common.utils.properties.LyPropertiesUtil;
import cn.ly.base_common.utils.string.LyStringUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 针对tk mybatis 3.2.0以上
 * 注：获取不到分页的sql(PageHelper处理的)
 * Created by liaomengge on 17/10/15.
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})}
)
public class SqlInterceptor implements Interceptor {

    private static final Logger log = LyLogger.getInstance(SqlInterceptor.class);

    private Boolean isEnableSqlLog = Boolean.FALSE;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();

        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = invocation.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long sqlCostTime = endTime - startTime;
            String sql = this.getSql(configuration, boundSql);
            this.formatSqlLog(mappedStatement.getSqlCommandType(), sqlId, sql, sqlCostTime, result);
        }

        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (BooleanUtils.isTrue(isEnableSqlLog) && target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        isEnableSqlLog = LyPropertiesUtil.getBooleanProperty(properties, "isEnableSqlLog", Boolean.FALSE);
    }

    /**
     * 获取完整的sql语句
     *
     * @param configuration
     * @param boundSql
     * @return
     */
    private String getSql(Configuration configuration, BoundSql boundSql) {
        // 输入sql字符串空判断
        String sql = boundSql.getSql();
        if (StringUtils.isBlank(sql)) {
            return "";
        }

        //美化sql
        sql = this.beautifySql(sql);

        //填充占位符, 目前基本不用mybatis存储过程调用,故此处不做考虑
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (!parameterMappings.isEmpty() && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = this.replacePlaceholder(sql, parameterObject);
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = this.replacePlaceholder(sql, obj);
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = this.replacePlaceholder(sql, obj);
                    }
                }
            }
        }
        return sql;
    }

    /**
     * 美化Sql
     */
    private String beautifySql(String sql) {
        return sql.replaceAll("[\\s\n ]+", " ");
    }

    /**
     * 填充占位符?
     *
     * @param sql
     * @param parameterObject
     * @return
     */
    private String replacePlaceholder(String sql, Object parameterObject) {
        String result;
        if (parameterObject instanceof String) {
            result = "'" + parameterObject.toString() + "'";
        } else if (parameterObject instanceof Date) {
            result = "'" + LyDateUtil.getDate2String((Date) parameterObject) + "'";
        } else if (parameterObject instanceof LocalDateTime) {
            result = "'" + LyJdk8DateUtil.getDate2String((LocalDateTime) parameterObject) + "'";
        } else if (parameterObject instanceof LocalDate) {
            result = "'" + LyJdk8DateUtil.getDate2String((LocalDate) parameterObject) + "'";
        } else if (parameterObject instanceof LocalTime) {
            result = "'" + LyJdk8DateUtil.getDate2String((LocalTime) parameterObject) + "'";
        } else {
            result = LyStringUtil.getValue(parameterObject);
        }
        return sql.replaceFirst("\\?", result);
    }

    /**
     * 格式化sql日志
     *
     * @param sqlCommandType
     * @param sqlId
     * @param sql
     * @param costTime
     */
    private void formatSqlLog(SqlCommandType sqlCommandType, String sqlId, String sql, long costTime, Object obj) {
        String sqlLog = "Mapper Method ===> [" + sqlId + "], " + sql + ", " + "Spend Time ===> " + costTime + " ms";
        if (sqlCommandType == SqlCommandType.UPDATE || sqlCommandType == SqlCommandType.INSERT
                || sqlCommandType == SqlCommandType.DELETE) {
            sqlLog += ", Affect Count ===> " + LyMoreNumberUtil.toInt(LyStringUtil.getValue(obj));
        }
        log.info(sqlLog);
    }
}
