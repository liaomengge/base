package cn.mwee.service.base_framework.mysql.extend;

import cn.mwee.service.base_framework.mysql.extend.special.*;
import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * Created by liaomengge on 2019/9/5.
 */
@RegisterMapper
public interface ExtendMapper<T> extends
        Insert4PgMapper<T>,
        InsertOrUpdateSelectiveMapper<T>,
        InsertListSelectiveMapper<T>,
        Select4MasterMapper<T>,
        SelectByExample4MasterMapper<T>,
        SelectExtendMapper<T>,
        SelectByExampleExtendMapper<T> {
}
