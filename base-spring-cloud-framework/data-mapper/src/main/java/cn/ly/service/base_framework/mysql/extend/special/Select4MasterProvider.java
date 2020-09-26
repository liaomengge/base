package cn.ly.service.base_framework.mysql.extend.special;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;

import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.provider.base.BaseSelectProvider;

/**
 * Created by liaomengge on 2019/11/20.
 */
public class Select4MasterProvider extends BaseSelectProvider {

    private static final String LIMIT_SUFFIX = "LIMIT 1";

    public Select4MasterProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String selectOneLimit4Master(MappedStatement ms) {
        String sql = super.selectOne(ms);
        if (!StringUtils.endsWithIgnoreCase(sql, LIMIT_SUFFIX)) {
            sql += " " + LIMIT_SUFFIX;
        }
        return sql;
    }

    public String select4Master(MappedStatement ms) {
        return super.select(ms);
    }

    public String selectAll4Master(MappedStatement ms) {
        return super.selectAll(ms);
    }

    public String selectByPrimaryKey4Master(MappedStatement ms) {
        return super.selectByPrimaryKey(ms);
    }

    public String selectCount4Master(MappedStatement ms) {
        return super.selectCount(ms);
    }
}
