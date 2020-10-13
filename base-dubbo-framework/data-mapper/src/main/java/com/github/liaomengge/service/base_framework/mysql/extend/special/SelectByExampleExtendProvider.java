package com.github.liaomengge.service.base_framework.mysql.extend.special;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.provider.ExampleProvider;

/**
 * Created by liaomengge on 2019/12/3.
 */
public class SelectByExampleExtendProvider extends ExampleProvider {

    private static final String LIMIT_SUFFIX = "LIMIT 1";

    public SelectByExampleExtendProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String selectOneLimitByExample(MappedStatement ms) {
        String sql = super.selectByExample(ms);
        if (!StringUtils.endsWithIgnoreCase(sql, LIMIT_SUFFIX)) {
            sql += " " + LIMIT_SUFFIX;
        }
        return sql;
    }
}
