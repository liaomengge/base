package cn.mwee.service.base_framework.mysql.mapper;

import cn.mwee.service.base_framework.mysql.extend.ExtendMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

/**
 * 由于数据库中一些奇葩的字段,用通用的Mapper查询时,会出现sql异常,故覆写其中的查询方法
 * Created by liaomengge on 16/8/30.
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T>, ExtendMapper<T> {

    T query(T t);

    List<T> query4List(T t);

    int queryCount(T t);
}
