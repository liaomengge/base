package cn.mwee.service.base_framework.mysql.extend.special;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;

/**
 * Created by liaomengge on 2019/9/5.
 */
public class InsertOrUpdateSelectiveProvider extends MapperTemplate {

    public InsertOrUpdateSelectiveProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String insertOrUpdateSelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        return SqlHelper.insertIntoTable(entityClass, tableName(entityClass)) +
                SqlHelper.insertColumns(entityClass, false, true, true) +
                SqlHelper.insertValuesColumns(entityClass, false, true, true) +
                "ON DUPLICATE KEY UPDATE " +
                updateValuesColumns(entityClass, false, true, true);
    }

    private String updateValuesColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");

        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (skipId && column.isId()) {
                continue;
            }
            if (notNull) {
                sql.append(getIfNotNull(column, column.getColumnHolder() + ",", notEmpty));

            } else {
                sql.append(column.getColumn()).append("= VALUES(").append(column.getColumn()).append("),");
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    private String getIfNotNull(EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"");
        sql.append(column.getProperty()).append(" != null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" and ");
            sql.append(column.getProperty()).append(" != '' ");
        }
        sql.append("\">");
        sql.append(column.getColumn());
        sql.append("= VALUES(");
        sql.append(column.getColumn());
        sql.append("),");
        sql.append("</if>");
        return sql.toString();
    }
}
