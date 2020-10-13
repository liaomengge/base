package com.github.liaomengge.service.base_framework.mysql.extend.special;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.provider.ExampleProvider;

/**
 * Created by liaomengge on 2019/11/20.
 */
public class SelectByExample4MasterProvider extends ExampleProvider {

    private static final String LIMIT_SUFFIX = "LIMIT 1";

    public SelectByExample4MasterProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String selectOneLimitByExample4Master(MappedStatement ms) {
        String sql = super.selectByExample(ms);
        if (!StringUtils.endsWithIgnoreCase(sql, LIMIT_SUFFIX)) {
            sql += " " + LIMIT_SUFFIX;
        }
        return sql;
    }

    public String selectByExample4Master(MappedStatement ms) {
        return super.selectByExample(ms);
    }

    public String selectCountByExample4Master(MappedStatement ms) {
        return super.selectCountByExample(ms);
    }
}
