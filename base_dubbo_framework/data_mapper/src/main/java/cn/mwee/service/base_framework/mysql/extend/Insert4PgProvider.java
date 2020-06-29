package cn.mwee.service.base_framework.mysql.extend;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.provider.base.BaseInsertProvider;

/**
 * Created by liaomengge on 2019/4/23.
 */
public class Insert4PgProvider extends BaseInsertProvider {

    public Insert4PgProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String insert4Pg(MappedStatement ms) {
        return super.insert(ms);
    }

    public String insertSelective4Pg(MappedStatement ms) {
        return super.insertSelective(ms);
    }
}
