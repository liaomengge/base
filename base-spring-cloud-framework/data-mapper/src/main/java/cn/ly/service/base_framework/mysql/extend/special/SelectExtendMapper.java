package cn.ly.service.base_framework.mysql.extend.special;

import org.apache.ibatis.annotations.SelectProvider;

import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * Created by liaomengge on 2019/12/3.
 */
@RegisterMapper
public interface SelectExtendMapper<T> {

    @SelectProvider(type = SelectExtendProvider.class, method = "dynamicSQL")
    T selectOneLimit(T record);
}
