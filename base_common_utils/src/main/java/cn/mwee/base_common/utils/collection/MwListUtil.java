package cn.mwee.base_common.utils.collection;

import com.google.common.base.Function;
import com.google.common.collect.*;
import lombok.experimental.UtilityClass;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by liaomengge on 16/4/20.
 */
@UtilityClass
public class MwListUtil {

    /**
     * 获取第一个元素, 如果List为空返回 null.
     */
    public <T> T getFirst(List<T> list) {
        if (CollectionUtils.isEmpty(list)) return null;
        return list.get(0);
    }

    /**
     * 获取最后一个元素, 如果List为空返回null.
     */
    public <T> T getLast(List<T> list) {
        if (CollectionUtils.isEmpty(list)) return null;

        return list.get(list.size() - 1);
    }

    /**********************************************集合运算********************************************/

    /**
     * list1,list2的并集（在list1或list2中的对象）, 产生新List
     * <p>
     * 对比Apache Common Collection4 ListUtils, 优化了初始大小
     */
    public <E> List<E> union(List<? extends E> list1, List<? extends E> list2) {
        List<E> result = new ArrayList<>(list1.size() + list2.size());
        result.addAll(list1);
        result.addAll(list2);
        return result;
    }

    /**
     * list1, list2的交集（同时在list1和list2的对象）, 产生新List
     * <p>
     * from Apache Common Collection4 ListUtils, 但其做了不合理的去重, 因此重新改为性能较低但不去重的版本
     * <p>
     * 与List.retainAll()相比, 考虑了的List中相同元素出现的次数, 如"a"在list1出现两次, 而在list2中只出现一次, 则交集里会保留一个"a".
     */
    public <T> List<T> intersection(List<? extends T> list1, List<? extends T> list2) {
        List<? extends T> smaller = list1;
        List<? extends T> larger = list2;
        if (list1.size() > list2.size()) {
            smaller = list2;
            larger = list1;
        }

        // 克隆一个可修改的副本
        List<T> newSmaller = new ArrayList<>(smaller);
        List<T> result = new ArrayList<>(smaller.size());
        for (T e : larger)
            if (newSmaller.contains(e)) {
                result.add(e);
                newSmaller.remove(e);
            }
        return result;
    }

    /**
     * list1, list2的差集（在list1, 不在list2中的对象）, 产生新List.
     * <p>
     * 与List.removeAll()相比, 会计算元素出现的次数, 如"a"在list1出现两次, 而在list2中只出现一次, 则差集里会保留一个"a".
     */
    public <T> List<T> difference(List<? extends T> list1, List<? extends T> list2) {
        List<T> result = new ArrayList<>(list1);
        Iterator<? extends T> iterator = list2.iterator();

        while (iterator.hasNext()) result.remove(iterator.next());

        return result;
    }

    /**
     * list1, list2的补集（在list1或list2中, 但不在交集中的对象, 又叫反交集）产生新List.
     * <p>
     * from Apache Common Collection4 ListUtils, 但其并集－交集时, 没有对交集*2, 所以做了修改
     */
    public <T> List<T> disjoint(List<? extends T> list1, List<? extends T> list2) {
        List<T> intersection = intersection(list1, list2);
        List<T> towIntersection = union(intersection, intersection);
        return difference(union(list1, list2), towIntersection);
    }


    public <T> Map<String, T> list2Map(List<T> list, String key, boolean isSort) {
        if (list == null) return null;

        Map<String, T> map;
        if (isSort) map = new TreeMap<>();
        else map = new HashMap<>(list.size());

        for (T t : list)
            try {
                map.put(BeanUtils.getProperty(t, key), t);
            } catch (Exception e) {
                return map;
            }
        return map;
    }

    /**********************************************
     * 转换
     ********************************************/

    public <T> Map<String, Collection<T>> transformMap(List<T> list, Function<T, String> function) {
        if (list == null) return null;

        Multimap<String, T> resultMap = ArrayListMultimap.create();
        for (T t : list) {
            String value = function.apply(t);
            if (StringUtils.isNotBlank(value)) resultMap.put(value, t);
        }
        return resultMap.asMap();
    }

    /**
     * list 2 map 转换
     *
     * @param list
     * @param key
     * @param <T>
     * @return
     */
    public <T> Map<String, Collection<T>> transformMap(List<T> list, String key) {
        return transformMap(list, input -> {
            try {
                return BeanUtils.getProperty(input, key);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        });
    }

    public <T> Map<String, Collection<T>> transformMap2(List<T> list, Function<T, String> function) {
        if (list == null) return null;

        return Multimaps.index(list, function).asMap();
    }

    /**
     * list 2 map 转换
     * 转换的结果集是不可变的
     *
     * @param list
     * @param key
     * @param <T>
     * @return
     */
    public <T> Map<String, Collection<T>> transformMap2(List<T> list, String key) {
        return transformMap2(list, input -> {
            try {
                return BeanUtils.getProperty(input, key);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        });
    }

    public <T> void sortListByKey(List<Map<String, T>> list, String key) {
        sortListByKey(list, key, true);
    }

    public <T> void sortListByKey(List<Map<String, T>> list, String key, boolean asc) {
        if (list == null) return;

        Comparator<Map<String, T>> comparator = (o1, o2) -> {
            if (asc) return MapUtils.getString(o1, key).compareTo(MapUtils.getString(o2, key));
            return MapUtils.getString(o1, key).compareTo(MapUtils.getString(o2, key));
        };
        Collections.sort(list, Ordering.from(comparator));
    }

    public <T> List<T> transform(List<Map<String, T>> list, String key) {
        return Lists.transform(list, input -> {
            if (input != null) return input.get(key);
            return null;
        });
    }

    public <T> List<T> transform2(List<Map<String, T>> list, String key) {
        if (list == null) return null;
        List<T> resultList = Lists.newArrayList();
        list.stream().filter(map -> MapUtils.isNotEmpty(map)).forEach(map -> {
            T t = map.get(key);
            if (t != null) resultList.add(t);
        });
        return resultList;
    }

    /**
     * 移除集合中emtry的元素
     *
     * @param list
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> List<Map<K, V>> removeEmptyElement(List<Map<K, V>> list) {
        if (CollectionUtils.isEmpty(list)) return new ArrayList<>(16);
        for (int i = list.size() - 1; i >= 0; i--) {
            Map<K, V> element = list.get(i);
            if (MapUtils.isEmpty(element)) list.remove(i);
        }
        return list;
    }
}
