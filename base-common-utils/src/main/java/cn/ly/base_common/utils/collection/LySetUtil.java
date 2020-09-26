package cn.ly.base_common.utils.collection;

import com.google.common.collect.Sets;

import java.util.Set;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 17/11/23.
 */
@UtilityClass
public class LySetUtil {

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
