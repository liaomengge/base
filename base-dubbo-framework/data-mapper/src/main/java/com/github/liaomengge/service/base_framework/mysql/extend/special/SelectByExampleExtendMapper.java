package com.github.liaomengge.service.base_framework.mysql.extend.special;

import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * Created by liaomengge on 2019/12/3.
 */
@RegisterMapper
public interface SelectByExampleExtendMapper<T> {

    /**
     * 根据Example条件进行查询,只查询一条,LIMIT限制
     *
     * @param example
     * @return
     */
    @SelectProvider(type = SelectByExampleExtendProvider.class, method = "dynamicSQL")
    T selectOneLimitByExample(Object example);
}
