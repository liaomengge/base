package cn.ly.service.base_framework.mysql.extend.special;

import cn.ly.base_common.support.datasource.annotation.Master;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * Created by liaomengge on 2019/11/20.
 */
@RegisterMapper
public interface Select4MasterMapper<T> {

    /**
     * 根据实体中的属性进行查询, 只能有一个返回值
     *
     * @param record
     * @return
     */
    @Master
    @SelectProvider(type = Select4MasterProvider.class, method = "dynamicSQL")
    T selectOneLimit4Master(T record);

    /**
     * 根据实体中的属性值进行查询, 查询条件使用等号
     *
     * @param record
     * @return
     */
    @Master
    @SelectProvider(type = Select4MasterProvider.class, method = "dynamicSQL")
    List<T> select4Master(T record);

    /**
     * 查询全部结果
     *
     * @return
     */
    @Master
    @SelectProvider(type = Select4MasterProvider.class, method = "dynamicSQL")
    List<T> selectAll4Master();

    /**
     * 根据主键字段进行查询, 方法参数必须包含完整的主键属性, 查询条件使用等号
     *
     * @param key
     * @return
     */
    @Master
    @SelectProvider(type = Select4MasterProvider.class, method = "dynamicSQL")
    T selectByPrimaryKey4Master(Object key);

    /**
     * 根据实体中的属性查询总数, 查询条件使用等号
     *
     * @param record
     * @return
     */
    @Master
    @SelectProvider(type = Select4MasterProvider.class, method = "dynamicSQL")
    int selectCount4Master(T record);
}
