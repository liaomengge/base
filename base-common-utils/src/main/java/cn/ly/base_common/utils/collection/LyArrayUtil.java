package cn.ly.base_common.utils.collection;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liaomengge on 16/9/6.
 */
@UtilityClass
public class LyArrayUtil {

    /**
     * 过滤指定元素
     *
     * @param filterVal
     * @param array
     * @return
     */
    public String[] filter(String filterVal, String... array) {
        if (array == null) {
            return null;
        }
        for (int i = 0; i < array.length; i++) {
            if (filterVal.equals(array[i])) {
                return ArrayUtils.remove(array, i);
            }
        }
        return array;
    }

    /**
     * 传入类型与大小创建数组.
     */
    public <T> T[] newArray(Class<T> type, int length) {
        return (T[]) Array.newInstance(type, length);
    }

    /**
     * list.toArray() 返回的是Object[] 如果要有类型的数组话, 就要使用list.toArray(new String[list.size()]), 这里对调用做了简化
     */
    public <T> T[] toArray(List<T> list, Class<T> type) {
        return list.toArray((T[]) Array.newInstance(type, list.size()));
    }

    /**
     * 原版将数组转换为List.
     * <p>
     * 注意转换后的List不能写入, 否则抛出UnsupportedOperationException
     *
     * @see java.util.Arrays#asList(Object...)
     */
    public <T> List<T> asList(T... a) {
        return Arrays.asList(a);
    }

    /**
     * 一个独立元素＋一个数组组成新的list, 只是一个View, 不复制数组内容, 而且独立元素在最前.
     * <p>
     * <p>
     * 注意转换后的List不能写入, 否则抛出UnsupportedOperationException
     *
     * @see com.google.common.collect.Lists#asList(Object, Object[])
     */
    public <E> List<E> asList(E first, E[] rest) {
        return Lists.asList(first, rest);
    }

    /**
     * Arrays.asList()的加强版, 返回一个底层为原始类型int的List
     * <p>
     * 与保存Integer相比节约空间, 同时只在读取数据时AutoBoxing.
     *
     * @see java.util.Arrays#asList(Object...)
     * @see com.google.common.primitives.Ints#asList(int...)
     */
    public List<Integer> intAsList(int... backingArray) {
        return Ints.asList(backingArray);
    }

    /**
     * Arrays.asList()的加强版, 返回一个底层为原始类型long的List
     * <p>
     * 与保存Long相比节约空间, 同时只在读取数据时AutoBoxing.
     *
     * @see java.util.Arrays#asList(Object...)
     * @see com.google.common.primitives.Longs#asList(long...)
     */
    public List<Long> longAsList(long... backingArray) {
        return Longs.asList(backingArray);
    }

    /**
     * Arrays.asList()的加强版, 返回一个底层为原始类型double的List
     * <p>
     * 与保存Double相比节约空间, 同时也避免了AutoBoxing.
     *
     * @see java.util.Arrays#asList(Object...)
     * @see com.google.common.primitives.Doubles#asList(double...)
     */
    public List<Double> doubleAsList(double... backingArray) {
        return Doubles.asList(backingArray);
    }
}
