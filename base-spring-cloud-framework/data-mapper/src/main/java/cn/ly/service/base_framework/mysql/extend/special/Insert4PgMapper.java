package cn.ly.service.base_framework.mysql.extend.special;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * Created by liaomengge on 2019/4/23.
 */
@RegisterMapper
public interface Insert4PgMapper<T> {

    /**
     * 保存一个实体, null的属性也会保存, 不会使用数据库默认值
     *
     * @param record
     * @return
     */
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    @InsertProvider(type = Insert4PgProvider.class, method = "dynamicSQL")
    int insert4Pg(T record);

    /**
     * 保存一个实体, 会使用数据库默认值
     *
     * @param record
     * @return
     */
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    @InsertProvider(type = Insert4PgProvider.class, method = "dynamicSQL")
    int insertSelective4Pg(T record);
}
