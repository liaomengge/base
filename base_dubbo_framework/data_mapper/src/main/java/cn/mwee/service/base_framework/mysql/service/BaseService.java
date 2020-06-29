package cn.mwee.service.base_framework.mysql.service;

import cn.mwee.service.base_framework.mysql.mapper.BaseMapper;
import cn.mwee.service.base_framework.mysql.page.MysqlPagination;
import cn.mwee.service.base_framework.mysql.util.PageUtil;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * Created by liaomengge on 16/8/29.
 */
public abstract class BaseService<T> {

    @Autowired
    private BaseMapper<T> baseMapper;

    /**
     * 自定义查询
     *
     * @param t
     * @return
     */
    public T query(T t) {
        return baseMapper.query(t);
    }

    /**
     * 依据主键查询
     *
     * @param obj
     * @return
     */
    public T queryByPrimaryKey(Object obj) {
        return baseMapper.selectByPrimaryKey(obj);
    }

    /**
     * 依据主键查询
     *
     * @param obj
     * @return
     */
    public T selectByPrimaryKey(Object obj) {
        return baseMapper.selectByPrimaryKey(obj);
    }

    /**
     * 自定义多条件查询
     *
     * @param t
     * @return
     */
    public List<T> query4List(T t) {
        return baseMapper.query4List(t);
    }

    /**
     * 查询多结果集返回
     *
     * @param t
     * @return
     */
    public List<T> select4List(T t) {
        return baseMapper.select(t);
    }

    /**
     * 自定义查总数
     *
     * @param t
     * @return
     */
    public int queryCount(T t) {
        return baseMapper.queryCount(t);
    }

    /**
     * 查询记录数
     *
     * @param t
     * @return
     */
    public int selectCount(T t) {
        return baseMapper.selectCount(t);
    }

    /**
     * 分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param select
     * @return
     */
    public PageInfo<T> query4Page(int pageNo, int pageSize, ISelect select) {
        return PageUtil.select4Page(pageNo, pageSize, select);
    }

    /**
     * 分页查询
     *
     * @param pagination
     * @param select
     * @return
     */
    public PageInfo<T> query4Page(MysqlPagination pagination, ISelect select) {
        return PageUtil.select4Page(pagination, select);
    }

    /**
     * 默认值,会自动插入 - mysql,oracle
     *
     * @param t
     * @return
     */
    public int insertSelective(T t) {
        return baseMapper.insertSelective(t);
    }

    /**
     * 默认值,会自动插入 - pg
     *
     * @param t
     * @return
     */
    public int insertSelective4Pg(T t) {
        return baseMapper.insertSelective4Pg(t);
    }

    /**
     * 存在则更新；不存在, 则插入
     * 默认值,会自动插入
     * 只更新非null的值
     *
     * @param t
     * @return
     */
    public int insertOrUpdateSelective(T t) {
        return baseMapper.insertOrUpdateSelective(t);
    }

    /**
     * 必须且仅会依据主键更新,否则不会update
     *
     * @param t
     * @return
     */
    public int updateByPrimaryKeySelective(T t) {
        return baseMapper.updateByPrimaryKeySelective(t);
    }

    /**
     * 属性字段必须和javabean一致
     *
     * @param obj
     * @return
     */
    public int deleteByPrimaryKey(Object obj) {
        return baseMapper.deleteByPrimaryKey(obj);
    }

    /*****************************************华丽的分割线***************************************/

    /**
     * 依据主键查询
     *
     * @param t
     * @return
     */
    public Optional<T> queryToOptional(T t) {
        return Optional.ofNullable(baseMapper.query(t));
    }

    public Optional<T> queryByPrimaryKeyToOptional(Object obj) {
        return Optional.ofNullable(baseMapper.selectByPrimaryKey(obj));
    }

    /**
     * 依据主键查询
     *
     * @param obj
     * @return
     */
    public Optional<T> selectByPrimaryKeyToOptional(Object obj) {
        return Optional.ofNullable(baseMapper.selectByPrimaryKey(obj));
    }

    /**
     * 自定义多条件查询
     *
     * @param t
     * @return
     */
    public Optional<List<T>> query4ListToOptional(T t) {
        return Optional.ofNullable(baseMapper.query4List(t));
    }

    /**
     * 自定义多条件查询
     *
     * @param t
     * @return
     */
    public Optional<List<T>> select4ListToOptional(T t) {
        return Optional.ofNullable(baseMapper.select(t));
    }

    public Optional<PageInfo<T>> query4PageToOptional(int pageNo, int pageSize, ISelect select) {
        return Optional.ofNullable(PageUtil.select4Page(pageNo, pageSize, select));
    }

    /**
     * 分页查询
     *
     * @param pagination
     * @param select
     * @return
     */
    public Optional<PageInfo<T>> query4PageToOptional(MysqlPagination pagination, ISelect select) {
        return Optional.ofNullable(PageUtil.select4Page(pagination, select));
    }
}
