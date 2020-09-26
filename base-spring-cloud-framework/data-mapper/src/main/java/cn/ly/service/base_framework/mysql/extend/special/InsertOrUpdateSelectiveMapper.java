package cn.ly.service.base_framework.mysql.extend.special;

import org.apache.ibatis.annotations.InsertProvider;

import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * Created by liaomengge on 2019/9/5.
 */
@RegisterMapper
public interface InsertOrUpdateSelectiveMapper<T> {

    /**
     * insertOrUpdate ON DUPLICATE KEY UPDATE
     *
     * @param t
     * @return
     */
    @InsertProvider(type = InsertOrUpdateSelectiveProvider.class, method = "dynamicSQL")
    int insertOrUpdateSelective(T t);
}
