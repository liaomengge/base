package cn.ly.base_common.utils.collection;

import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by liaomengge on 17/11/23.
 */
@UtilityClass
public class LyCollectionUtil {

    /**
     * 取得Collection的第一个元素, 如果collection为空返回null.
     */
    public <T> T getFirst(Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) return null;
        if (collection instanceof List) return ((List<T>) collection).get(0);
        return collection.iterator().next();
    }

    /**
     * 获取Collection的最后一个元素, 如果collection为空返回null.
     */
    public <T> T getLast(Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) return null;

        // 当类型List时, 直接取得最后一个元素.
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }

        return Iterators.getLast(collection.iterator());
    }

    /**
     * 返回无序集合中的最小值, 使用元素默认排序
     */
    public <T extends Object & Comparable<? super T>> T min(Collection<? extends T> coll) {
        return Collections.min(coll);
    }

    /**
     * 返回无序集合中的最小值
     */
    public <T> T min(Collection<? extends T> coll, Comparator<? super T> comp) {
        return Collections.min(coll, comp);
    }

    /**
     * 返回无序集合中的最大值, 使用元素默认排序
     */
    public <T extends Object & Comparable<? super T>> T max(Collection<? extends T> coll) {
        return Collections.max(coll);
    }

    /**
     * 返回无序集合中的最大值
     */
    public <T> T max(Collection<? extends T> coll, Comparator<? super T> comp) {
        return Collections.max(coll, comp);
    }

    /**
     * 返回无序集合中的最小值和最大值, 使用元素默认排序
     */
    public <T extends Object & Comparable<? super T>> Pair<T, T> minAndMax(Collection<? extends T> coll) {
        Iterator<? extends T> i = coll.iterator();
        T minCandidate = i.next();
        T maxCandidate = minCandidate;

        while (i.hasNext()) {
            T next = i.next();
            if (next.compareTo(minCandidate) < 0) minCandidate = next;
            else if (next.compareTo(maxCandidate) > 0) maxCandidate = next;
        }
        return Pair.of(minCandidate, maxCandidate);
    }

    /**
     * 返回无序集合中的最小值和最大值
     */
    public <T> Pair<T, T> minAndMax(Collection<? extends T> coll, Comparator<? super T> comp) {

        Iterator<? extends T> i = coll.iterator();
        T minCandidate = i.next();
        T maxCandidate = minCandidate;

        while (i.hasNext()) {
            T next = i.next();
            if (comp.compare(next, minCandidate) < 0) minCandidate = next;
            else if (comp.compare(next, maxCandidate) > 0) maxCandidate = next;
        }

        return Pair.of(minCandidate, maxCandidate);
    }

    /**
     * 排序最高的N个对象, guava已优化.
     */
    public <T extends Comparable> List<T> topN(Iterable<T> iterable, int n) {
        return Ordering.natural().greatestOf(iterable, n);
    }

    /**
     * 排序最高的N个对象, guava已优化.
     */
    public <T extends Comparable> List<T> topN(Iterator<T> iterator, int n) {
        return Ordering.natural().greatestOf(iterator, n);
    }

    /**
     * 排序最高的N个对象, guava已优化.
     */
    public <T> List<T> topN(Iterable<T> iterable, int n, Comparator<T> comp) {
        return Ordering.from(comp).greatestOf(iterable, n);
    }

    /**
     * 排序最高的N个对象, guava已优化.
     */
    public <T> List<T> topN(Iterator<T> iterator, int n, Comparator<T> comp) {
        return Ordering.from(comp).greatestOf(iterator, n);
    }

    /**
     * 排序最低的N个对象, guava已优化.
     */
    public <T extends Comparable> List<T> bottomN(Iterable<T> iterable, int n) {
        return Ordering.natural().leastOf(iterable, n);
    }

    /**
     * 排序最低的N个对象, guava已优化.
     */
    public <T extends Comparable> List<T> bottomN(Iterator<T> iterator, int n) {
        return Ordering.natural().leastOf(iterator, n);
    }

    /**
     * 排序最低的N个对象, guava已优化.
     */
    public <T> List<T> bottomN(Iterable<T> coll, int n, Comparator<T> comp) {
        return Ordering.from(comp).leastOf(coll, n);
    }

    /**
     * 排序最低的N个对象, guava已优化.
     */
    public <T> List<T> bottomN(Iterator<T> iterator, int n, Comparator<T> comp) {
        return Ordering.from(comp).leastOf(iterator, n);
    }
}
