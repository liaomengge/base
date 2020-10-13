package com.github.liaomengge.service.base_framework.mysql.extend.special;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.provider.base.BaseSelectProvider;

/**
 * Created by liaomengge on 2019/12/3.
 */
public class SelectExtendProvider extends BaseSelectProvider {

    private static final String LIMIT_SUFFIX = "LIMIT 1";

    public SelectExtendProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String selectOneLimit(MappedStatement ms) {
        String sql = super.selectOne(ms);
        if (!StringUtils.endsWithIgnoreCase(sql, LIMIT_SUFFIX)) {
            sql += " " + LIMIT_SUFFIX;
        }
        return sql;
    }
}
