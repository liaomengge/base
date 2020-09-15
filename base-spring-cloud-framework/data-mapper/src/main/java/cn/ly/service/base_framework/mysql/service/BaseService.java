package cn.ly.service.base_framework.mysql.service;

import cn.ly.service.base_framework.mysql.mapper.BaseMapper;
import cn.ly.service.base_framework.mysql.page.MysqlPagination;
import cn.ly.service.base_framework.mysql.util.PageUtil;
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

    /*****************************一些不规则的表字段可能需要自己自定义*****************************/

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
     * 自定义多条件查询
     *
     * @param t
     * @return
     */
    public List<T> query4List(T t) {
        return baseMapper.query4List(t);
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

    /*****************************************基础通用读处理*************************************/

    /**
     * 按指定字段查询记录
     *
     * @param t
     * @return
     */
    public List<T> select(T t) {
        return baseMapper.select(t);
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
     * 查询所有记录
     *
     * @return
     */
    public List<T> selectAll() {
        return baseMapper.selectAll();
    }

    /**
     * 查询单条记录
     *
     * @param t
     * @return
     */
    public T selectOneLimit(T t) {
        return baseMapper.selectOneLimit(t);
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
     * 查询单条记录
     */
    public T selectOneLimitByExample(Object example) {
        return baseMapper.selectOneLimitByExample(example);
    }

    /**
     * 分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param select
     * @return
     */
    public PageInfo<T> select4Page(int pageNo, int pageSize, ISelect select) {
        return PageUtil.select4Page(pageNo, pageSize, select);
    }

    /**
     * 分页查询
     *
     * @param pagination
     * @param select
     * @return
     */
    public PageInfo<T> select4Page(MysqlPagination pagination, ISelect select) {
        return PageUtil.select4Page(pagination, select);
    }

    /***************************************基础通用读处理(走主库)*************************************/

    /**
     * 按指定字段查询记录,走主
     *
     * @param t
     * @return
     */
    public List<T> select4Master(T t) {
        return baseMapper.select4Master(t);
    }

    /**
     * 查询记录数,走主
     *
     * @param t
     * @return
     */
    public int selectCount4Master(T t) {
        return baseMapper.selectCount4Master(t);
    }

    /**
     * 查询所有记录,走主
     *
     * @return
     */
    public List<T> selectAll4Master() {
        return baseMapper.selectAll4Master();
    }

    /**
     * 查询单条记录,走主
     *
     * @param t
     * @return
     */
    public T selectOneLimit4Master(T t) {
        return baseMapper.selectOneLimit4Master(t);
    }

    /**
     * 依据主键查询,走主
     *
     * @param obj
     * @return
     */
    public T selectByPrimaryKey4Master(Object obj) {
        return baseMapper.selectByPrimaryKey4Master(obj);
    }

    /**
     * 查询单条记,走主
     */
    public T selectOneLimitByExample4Master(Object example) {
        return baseMapper.selectOneLimitByExample4Master(example);
    }

    /**
     * 查记录example, 走主
     *
     * @param example
     * @return
     */
    public List<T> selectByExample4Master(Object example) {
        return baseMapper.selectByExample4Master(example);
    }

    /**
     * 查记录数example, 走主
     *
     * @param example
     * @return
     */
    public int selectCountByExample4Master(Object example) {
        return baseMapper.selectCountByExample4Master(example);
    }

    /**************************************基础通用写处理**************************************/

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
     * 批量插入, 插入默认值
     * 一条失败, 批量的数据全部失败
     *
     * @param list
     * @return
     */
    public int insertListSelective(List<T> list) {
        return baseMapper.insertListSelective(list);
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

    /************************************************华丽的分割线*******************************************************/

    /**
     * 自定义查询
     *
     * @param t
     * @return
     */
    public Optional<T> queryToOptional(T t) {
        return Optional.ofNullable(baseMapper.query(t));
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
     * 按指定字段查询记录
     *
     * @param t
     * @return
     */
    public Optional<List<T>> selectToOptional(T t) {
        return Optional.ofNullable(baseMapper.select(t));
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public Optional<List<T>> selectAllToOptional() {
        return Optional.ofNullable(baseMapper.selectAll());
    }

    /**
     * 查询单条记录
     *
     * @param t
     * @return
     */
    public Optional<T> selectOneLimitToOptional(T t) {
        return Optional.ofNullable(baseMapper.selectOneLimit(t));
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
     * 查询单条记录
     */
    public Optional<T> selectOneLimitByExampleToOptional(Object example) {
        return Optional.ofNullable(baseMapper.selectOneLimitByExample(example));
    }

    /**
     * 分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param select
     * @return
     */
    public Optional<PageInfo<T>> select4PageToOptional(int pageNo, int pageSize, ISelect select) {
        return Optional.ofNullable(PageUtil.select4Page(pageNo, pageSize, select));
    }

    /**
     * 分页查询
     *
     * @param pagination
     * @param select
     * @return
     */
    public Optional<PageInfo<T>> select4PageToOptional(MysqlPagination pagination, ISelect select) {
        return Optional.ofNullable(PageUtil.select4Page(pagination, select));
    }

    /**
     * 按指定字段查询记录,走主
     *
     * @param t
     * @return
     */
    public Optional<List<T>> select4MasterToOptional(T t) {
        return Optional.ofNullable(baseMapper.select4Master(t));
    }

    /**
     * 查询所有记录,走主
     *
     * @return
     */
    public Optional<List<T>> selectAll4MasterToOptional() {
        return Optional.ofNullable(baseMapper.selectAll4Master());
    }

    /**
     * 查询单条记录,走主
     *
     * @param t
     * @return
     */
    public Optional<T> selectOneLimit4MasterToOptional(T t) {
        return Optional.ofNullable(baseMapper.selectOneLimit4Master(t));
    }

    /**
     * 查询单条记,走主
     *
     * @param example
     * @return
     */
    public Optional<T> selectOneLimitByExample4MasterToOptional(Object example) {
        return Optional.ofNullable(baseMapper.selectOneLimitByExample4Master(example));
    }

    /**
     * 依据主键查询,走主
     *
     * @param obj
     * @return
     */
    public Optional<T> selectByPrimaryKey4MasterToOptional(Object obj) {
        return Optional.ofNullable(baseMapper.selectByPrimaryKey4Master(obj));
    }

    /**
     * 查记录example, 走主
     *
     * @param example
     * @return
     */
    public Optional<List<T>> selectByExample4MasterToOptional(Object example) {
        return Optional.ofNullable(baseMapper.selectByExample4Master(example));
    }
}
