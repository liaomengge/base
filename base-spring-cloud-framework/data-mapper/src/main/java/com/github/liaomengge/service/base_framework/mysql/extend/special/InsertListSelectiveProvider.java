package com.github.liaomengge.service.base_framework.mysql.extend.special;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.time.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by liaomengge on 2019/11/19.
 */
public class InsertListSelectiveProvider extends MapperTemplate {

    public InsertListSelectiveProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String insertListSelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"listNotEmptyCheck\" value=\"@tk.mybatis.mapper.util.OGNL@notEmptyCollectionCheck" +
                "(list, '" + ms.getId() + " 方法参数为空')\"/>");
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, true, false, isNotEmpty()));
        sql.append("VALUES  ");
        sql.append("<foreach collection=\"collection\" item=\"record\" separator=\",\" > ");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时, 不需要考虑他的属性是否为空, 因为如果为空, 一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isInsertable()) {
                // 如果插入字段值为null, 使用数据库默认值
                sql.append("IFNULL(");
                sql.append(column.getColumnHolder("record") + ",");
                // 判读是否是日期
                if (isDate(column)) {
                    sql.append("IF(DEFAULT(")
                            .append(column.getColumn())
                            .append(")='0000-00-00 00:00:00',CURRENT_TIMESTAMP,DEFAULT(")
                            .append(column.getColumn()).append("))");
                } else {
                    sql.append("DEFAULT(")
                            .append(column.getColumn())
                            .append(")");
                }
                sql.append("),");
            }
        }
        sql.append("</trim>");
        sql.append(" </foreach>");
        // 反射把MappedStatement中的设置主键名
        EntityHelper.setKeyProperties(EntityHelper.getPKColumns(entityClass), ms);
        return sql.toString();
    }

    private boolean isDate(EntityColumn column) {
        Class<?> javaType = column.getJavaType();
        if (Date.class.isAssignableFrom(javaType)) {
            return true;
        }
        // 兼容java8
        if (LocalDateTime.class.isAssignableFrom(javaType) || LocalDate.class.isAssignableFrom(javaType) || LocalTime.class.isAssignableFrom(javaType)
                || OffsetTime.class.isAssignableFrom(javaType) || OffsetDateTime.class.isAssignableFrom(javaType)) {
            return true;
        }
        return false;
    }
}
