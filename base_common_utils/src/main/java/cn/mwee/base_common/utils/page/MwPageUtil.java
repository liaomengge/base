package cn.mwee.base_common.utils.page;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * Created by liaomengge on 2018/8/9.
 */
@UtilityClass
public class MwPageUtil {

    /**
     * @param t                 请求参数对象
     * @param pageSizeFunction  分页大小pageSize(获取分页大小pageSize)
     * @param transformOperator 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param <T>
     * @param <R>
     * @param <V>
     */
    public <T, R, V> void process(T t, ToIntFunction<T> pageSizeFunction, UnaryOperator<T> transformOperator,
                                  Function<T, R> handleFunction, Function<R, List<V>> retFunction) {
        int size = pageSizeFunction.applyAsInt(t);
        int initSize = size;
        while (initSize >= size) {
            Optional<R> optional = Optional.ofNullable(handleFunction.apply(t));
            List<V> retList = optional.map(retFunction).orElse(Collections.emptyList());
            initSize = retList.size();
            t = transformOperator.apply(t);
        }
    }

    /**
     * @param t                 请求参数对象
     * @param pageSizeFunction  分页大小pageSize(获取分页大小pageSize)
     * @param transformOperator 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param consumers         每次批处理消费
     * @param <T>
     * @param <R>
     * @param <V>
     */
    public <T, R, V> void process(T t, ToIntFunction<T> pageSizeFunction, UnaryOperator<T> transformOperator,
                                  Function<T, R> handleFunction, Function<R, List<V>> retFunction,
                                  Consumer<List<V>> consumers) {
        int size = pageSizeFunction.applyAsInt(t);
        int initSize = size;
        while (initSize >= size) {
            Optional<R> optional = Optional.ofNullable(handleFunction.apply(t));
            List<V> retList = optional.map(retFunction).orElse(Collections.emptyList());
            consumers.accept(retList);
            initSize = retList.size();
            t = transformOperator.apply(t);
        }
    }

    /**
     * @param t                 请求参数对象
     * @param pageSizeFunction  分页大小pageSize(获取分页大小pageSize)
     * @param transformOperator 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param <T>
     * @param <R>
     * @param <V>
     * @return 返回总结果集
     */
    public <T, R, V> List<V> processRet(T t, ToIntFunction<T> pageSizeFunction,
                                        UnaryOperator<T> transformOperator, Function<T, R> handleFunction,
                                        Function<R, List<V>> retFunction) {
        List<V> totalList = Lists.newArrayList();
        int size = pageSizeFunction.applyAsInt(t);
        int initSize = size;
        while (initSize >= size) {
            Optional<R> optional = Optional.ofNullable(handleFunction.apply(t));
            List<V> retList = optional.map(retFunction).orElse(Collections.emptyList());
            totalList.addAll(retList);
            initSize = retList.size();
            t = transformOperator.apply(t);
        }

        return totalList;
    }

    /***************************************************华丽的分割线*************************************************/

    /**
     * @param t                 请求参数对象
     * @param transformOperator 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param <T>
     */
    public <T extends Pagination, V> void process(T t, UnaryOperator<T> transformOperator,
                                                  Function<T, List<V>> handleFunction) {
        process(t, transformOperator, handleFunction, Function.identity());
    }

    /**
     * @param t                 请求参数对象
     * @param transformOperator 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param <T>
     * @param <R>
     * @param <V>
     */
    public <T extends Pagination, R, V> void process(T t, UnaryOperator<T> transformOperator,
                                                     Function<T, R> handleFunction,
                                                     Function<R, List<V>> retFunction) {
        process(t, T::getPageSize, transformOperator, handleFunction, retFunction);
    }

    /**
     * @param t                 请求参数对象
     * @param transformOperator 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param consumers         每次批处理消费
     * @param <T>
     */
    public <T extends Pagination, V> void process(T t, UnaryOperator<T> transformOperator,
                                                  Function<T, List<V>> handleFunction,
                                                  Consumer<List<V>> consumers) {
        process(t, transformOperator, handleFunction, Function.identity(), consumers);
    }

    /**
     * @param t                 请求参数对象
     * @param transformOperator 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param consumers         每次批处理消费
     * @param <T>
     * @param <R>
     * @param <V>
     */
    public <T extends Pagination, R, V> void process(T t, UnaryOperator<T> transformOperator,
                                                     Function<T, R> handleFunction,
                                                     Function<R, List<V>> retFunction,
                                                     Consumer<List<V>> consumers) {
        process(t, T::getPageSize, transformOperator, handleFunction, retFunction, consumers);
    }

    /**
     * @param t                 请求参数对象
     * @param transformOperator 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param <T>
     * @return 返回总结果集
     */
    public <T extends Pagination, V> List<V> processRet(T t, UnaryOperator<T> transformOperator,
                                                        Function<T, List<V>> handleFunction) {
        return processRet(t, transformOperator, handleFunction, Function.identity());
    }

    /**
     * @param t                 请求参数对象
     * @param transformOperator 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param <T>
     * @param <R>
     * @param <V>
     * @return 返回总结果集
     */
    public <T extends Pagination, R, V> List<V> processRet(T t, UnaryOperator<T> transformOperator,
                                                           Function<T, R> handleFunction,
                                                           Function<R, List<V>> retFunction) {
        return processRet(t, T::getPageSize, transformOperator, handleFunction, retFunction);
    }

    /*************************************************华丽的分割线*************************************************/

    /**
     * @param t                 请求参数对象
     * @param pageSizeFunction  分页大小pageSize(获取分页大小pageSize)
     * @param transformFunction 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param <T>
     * @param <R>
     * @param <V>
     */
    public <T, R, V> void process(T t, ToIntFunction<T> pageSizeFunction, Function<T, R> handleFunction,
                                  Function<R, List<V>> retFunction, Function<List<V>, T> transformFunction) {
        int size = pageSizeFunction.applyAsInt(t);
        int initSize = size;
        while (initSize >= size) {
            Optional<R> optional = Optional.ofNullable(handleFunction.apply(t));
            List<V> retList = optional.map(retFunction).orElse(Collections.emptyList());
            initSize = retList.size();
            t = transformFunction.apply(retList);
        }
    }

    /**
     * @param t                 请求参数对象
     * @param pageSizeFunction  分页大小pageSize(获取分页大小pageSize)
     * @param transformFunction 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param consumers         每次批处理消费
     * @param <T>
     * @param <R>
     * @param <V>
     */
    public <T, R, V> void process(T t, ToIntFunction<T> pageSizeFunction,
                                  Function<T, R> handleFunction, Function<R, List<V>> retFunction,
                                  Function<List<V>, T> transformFunction, Consumer<List<V>> consumers) {
        int size = pageSizeFunction.applyAsInt(t);
        int initSize = size;
        while (initSize >= size) {
            Optional<R> optional = Optional.ofNullable(handleFunction.apply(t));
            List<V> retList = optional.map(retFunction).orElse(Collections.emptyList());
            consumers.accept(retList);
            initSize = retList.size();
            t = transformFunction.apply(retList);
        }
    }

    /**
     * @param t                 请求参数对象
     * @param pageSizeFunction  分页大小pageSize(获取分页大小pageSize)
     * @param transformFunction 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param <T>
     * @param <R>
     * @param <V>
     * @return 返回总结果集
     */
    public <T, R, V> List<V> processRet(T t, ToIntFunction<T> pageSizeFunction, Function<T, R> handleFunction,
                                        Function<R, List<V>> retFunction,
                                        Function<List<V>, T> transformFunction) {
        List<V> totalList = Lists.newArrayList();
        int size = pageSizeFunction.applyAsInt(t);
        int initSize = size;
        while (initSize >= size) {
            Optional<R> optional = Optional.ofNullable(handleFunction.apply(t));
            List<V> retList = optional.map(retFunction).orElse(Collections.emptyList());
            totalList.addAll(retList);
            initSize = retList.size();
            t = transformFunction.apply(retList);
        }

        return totalList;
    }

    /***************************************************华丽的分割线*************************************************/

    /**
     * @param t                 请求参数对象
     * @param transformFunction 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param <T>
     */
    public <T extends Pagination, V> void process(T t, Function<T, List<V>> handleFunction,
                                                  Function<List<V>, T> transformFunction) {
        process(t, handleFunction, Function.identity(), transformFunction);
    }

    /**
     * @param t                 请求参数对象
     * @param transformFunction 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param <T>
     * @param <R>
     * @param <V>
     */
    public <T extends Pagination, R, V> void process(T t, Function<T, R> handleFunction,
                                                     Function<R, List<V>> retFunction,
                                                     Function<List<V>, T> transformFunction) {
        process(t, T::getPageSize, handleFunction, retFunction, transformFunction);
    }

    /**
     * @param t                 请求参数对象
     * @param transformFunction 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param consumers         每次批处理消费
     * @param <T>
     */
    public <T extends Pagination, V> void process(T t, Function<T, List<V>> handleFunction,
                                                  Function<List<V>, T> transformFunction,
                                                  Consumer<List<V>> consumers) {
        process(t, handleFunction, Function.identity(), transformFunction, consumers);
    }

    /**
     * @param t                 请求参数对象
     * @param transformFunction 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param consumers         每次批处理消费
     * @param <T>
     * @param <R>
     * @param <V>
     */
    public <T extends Pagination, R, V> void process(T t, Function<T, R> handleFunction,
                                                     Function<R, List<V>> retFunction,
                                                     Function<List<V>, T> transformFunction,
                                                     Consumer<List<V>> consumers) {
        process(t, T::getPageSize, handleFunction, retFunction, transformFunction, consumers);
    }

    /**
     * @param t                 请求参数对象
     * @param transformFunction 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param <T>
     * @return 返回总结果集
     */
    public <T extends Pagination, V> List<V> processRet(T t, Function<T, List<V>> handleFunction,
                                                        Function<List<V>, T> transformFunction) {
        return processRet(t, handleFunction, Function.identity(), transformFunction);
    }

    /**
     * @param t                 请求参数对象
     * @param transformFunction 分页后请求转化(下一次分页page)
     * @param handleFunction    请求对象处理
     * @param retFunction       返回结果集处理
     * @param <T>
     * @param <R>
     * @param <V>
     * @return 返回总结果集
     */
    public <T extends Pagination, R, V> List<V> processRet(T t, Function<T, R> handleFunction,
                                                           Function<R, List<V>> retFunction,
                                                           Function<List<V>, T> transformFunction) {
        return processRet(t, T::getPageSize, handleFunction, retFunction, transformFunction);
    }

    @Getter
    @Setter
    public class Pagination {

        private int pageNo;
        private int pageSize;

        public Pagination() {
            this.pageNo = 1;
            this.pageSize = 100;
        }

        public Pagination(int pageNo, int pageSize) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
        }

        public Pagination nextPageNo() {
            this.pageNo = ++this.pageNo;
            return this;
        }
    }
}
