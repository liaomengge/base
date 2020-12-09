package com.github.liaomengge.base_common.utils.collection;

import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

/**
 * Created by liaomengge on 17/11/23.
 */
@UtilityClass
public class LySetUtil {

    /**
     * 获取第一个元素, 如果Set为空返回 null.
     */
    public <T> T getFirst(Set<T> set) {
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        if (set instanceof SortedSet) {
            return ((SortedSet<T>) set).first();
        }

        Iterator<T> it = set.iterator();
        T first = null;
        if (it.hasNext()) {
            first = it.next();
        }
        return first;
    }

    /**
     * 获取最后一个元素, 如果Set为空返回null.
     */
    public <T> T getLast(Set<T> set) {
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        if (set instanceof SortedSet) {
            return ((SortedSet<T>) set).last();
        }

        // Full iteration necessary...
        Iterator<T> it = set.iterator();
        T last = null;
        while (it.hasNext()) {
            last = it.next();
        }
        return last;
    }


    /**********************************************Set集合运算函数********************************************/
    /**
     * set1, set2的并集（在set1或set2的对象）的只读view, 不复制产生新的Set对象.
     * <p>
     * 如果尝试写入该View会抛出UnsupportedOperationException
     */
    public <E> Set<E> unionView(Set<? extends E> set1, Set<? extends E> set2) {
        return Sets.union(set1, set2);
    }

    /**
     * set1, set2的交集（同时在set1和set2的对象）的只读view, 不复制产生新的Set对象.
     * <p>
     * 如果尝试写入该View会抛出UnsupportedOperationException
     */
    public <E> Set<E> intersectionView(Set<E> set1, Set<?> set2) {
        return Sets.intersection(set1, set2);
    }

    /**
     * set1, set2的差集（在set1, 不在set2中的对象）的只读view, 不复制产生新的Set对象.
     * <p>
     * 如果尝试写入该View会抛出UnsupportedOperationException
     */
    public <E> Set<E> differenceView(Set<E> set1, Set<?> set2) {
        return Sets.difference(set1, set2);
    }

    /**
     * set1, set2的补集（在set1或set2中, 但不在交集中的对象, 又叫反交集）的只读view, 不复制产生新的Set对象.
     * <p>
     * 如果尝试写入该View会抛出UnsupportedOperationException
     */
    public <E> Set<E> disjointView(Set<? extends E> set1, Set<? extends E> set2) {
        return Sets.symmetricDifference(set1, set2);
    }
}
