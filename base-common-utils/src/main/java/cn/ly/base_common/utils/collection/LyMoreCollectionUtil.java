package cn.ly.base_common.utils.collection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2020/6/3.
 */
@UtilityClass
public class LyMoreCollectionUtil {

    public <T> List<T> toList(T[] t) {
        return Arrays.stream(t).collect(Collectors.toList());
    }

    public <T> Set<T> toSet(T[] t) {
        return Arrays.stream(t).collect(Collectors.toSet());
    }

    public <T> List<T> toFlatList(List<List<T>> list) {
        return list.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public <T> Set<T> toFlatSet(Set<Set<T>> set) {
        return set.stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    public <T> List<T> toFlatList2(List<T[]> list) {
        return list.stream().flatMap(Arrays::stream).collect(Collectors.toList());
    }

    public <T> Set<T> toFlatSet2(Set<T[]> set) {
        return set.stream().flatMap(Arrays::stream).collect(Collectors.toSet());
    }

    /******************************************************************************************/

    public <E, C extends Collection<E>> void removeIf(C c, Predicate<E> predicate, Consumer<E> consumer) {
        Iterator<E> iterator = c.iterator();
        while (iterator.hasNext()) {
            E next = iterator.next();
            if (predicate.test(next)) {
                iterator.remove();
            } else {
                consumer.accept(next);
            }
        }
    }

    public <K, V, M extends Map<K, V>> void removeIf(M m, Predicate<K> predicate,
                                                     Consumer<Map.Entry<K, V>> consumer) {
        Iterator<Map.Entry<K, V>> iterator = m.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> next = iterator.next();
            if (predicate.test(next.getKey())) {
                iterator.remove();
            } else {
                consumer.accept(next);
            }
        }
    }

    /******************************************************************************************/

    /**
     * list到list转换
     *
     * @param list
     * @param function
     * @param <T>
     * @param <K>
     * @return
     */
    public <T, K> List<K> convertList(List<T> list, Function<T, K> function) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return list.stream().map(function).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    /**
     * list到map转换
     *
     * @param list
     * @param keyFunc
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> Map<K, V> convertMap(List<V> list, Function<V, K> keyFunc) {
        return convertMap(list, keyFunc, Function.identity());
    }

    /**
     * list到map转换
     *
     * @param list
     * @param predicate
     * @param keyFunc
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> Map<K, V> convertMap(List<V> list, Predicate<V> predicate, Function<V, K> keyFunc) {
        return convertMap(list, predicate, keyFunc, Function.identity());
    }

    /**
     * list到map转换
     *
     * @param list
     * @param keyFunc
     * @param valFunc
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> Map<K, V> convertMap(List<V> list, Function<V, K> keyFunc, Function<V, V> valFunc) {
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.toMap(keyFunc, valFunc, (v1, v2) -> v2));
    }

    /**
     * list到map转换
     *
     * @param list
     * @param predicate
     * @param keyFunc
     * @param valFunc
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> Map<K, V> convertMap(List<V> list, Predicate<V> predicate, Function<V, K> keyFunc,
                                       Function<V, V> valFunc) {
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream().filter(predicate).collect(Collectors.toMap(keyFunc, valFunc, (v1, v2) -> v2));
    }

    /**
     * map填充list属性
     *
     * @param list
     * @param map
     * @param function
     * @param action
     * @param <T>
     * @param <K>
     * @param <V>
     */
    public <T, K, V> void fillList(List<T> list, Map<K, V> map, Function<T, K> function, Consumer<T> action) {
        if (CollectionUtils.isEmpty(list) || MapUtils.isEmpty(map)) {
            return;
        }
        list.stream().filter(val -> map.containsKey(function.apply(val))).forEach(action);
    }

    /**
     * fillList填充retList指定的属性
     *
     * @param retList
     * @param fillList
     * @param retEqFunc
     * @param fillCovertFunc
     * @param action
     * @param <T>
     * @param <K>
     * @param <V>
     */
    public <T, K, V> void fillList(List<T> retList, List<V> fillList, Function<T, K> retEqFunc,
                                   Function<V, K> fillCovertFunc, BiConsumer<T, Map<K, V>> action) {
        if (CollectionUtils.isEmpty(retList) || CollectionUtils.isEmpty(fillList)) {
            return;
        }
        Map<K, V> map = convertMap(fillList, fillCovertFunc);
        retList.stream().filter(val -> map.containsKey(retEqFunc.apply(val))).forEach(val -> action.accept(val, map));
    }

    /**
     * fillList填充retList指定的属性
     *
     * @param retList
     * @param fillList
     * @param fillPredicate
     * @param retEqFunc
     * @param fillCovertFunc
     * @param action
     * @param <T>
     * @param <K>
     * @param <V>
     */
    public <T, K, V> void fillList(List<T> retList, List<V> fillList, Predicate<V> fillPredicate,
                                   Function<T, K> retEqFunc,
                                   Function<V, K> fillCovertFunc, BiConsumer<T, Map<K, V>> action) {
        if (CollectionUtils.isEmpty(retList) || CollectionUtils.isEmpty(fillList)) {
            return;
        }
        Map<K, V> map = convertMap(fillList, fillPredicate, fillCovertFunc);
        retList.stream().filter(val -> map.containsKey(retEqFunc.apply(val))).forEach(val -> action.accept(val, map));
    }
}
