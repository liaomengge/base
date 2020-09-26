package cn.ly.base_common.support.predicate;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2020/6/3.
 */
@UtilityClass
public class _Predicates {

    public <T> Predicate nonNull(T obj) {
        return val -> Objects.nonNull(obj);
    }

    public <T> Predicate isNull(T obj) {
        return val -> Objects.isNull(obj);
    }

    public static <T> Predicate<T> and(Predicate<? super T>... components) {
        return new AndPredicate<>(Lists.newArrayList(components));
    }

    public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> components) {
        return new AndPredicate<>(Lists.newArrayList(components));
    }

    public static <T> Predicate<T> or(Predicate<? super T>... components) {
        return new OrPredicate<>(Lists.newArrayList(components));
    }

    public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> components) {
        return new OrPredicate<>(Lists.newArrayList(components));
    }

    /************************************************华丽的分割线*******************************************************/

    @AllArgsConstructor
    private static class OrPredicate<T> implements Predicate<T>, Serializable {
        private static final long serialVersionUID = -8375845185593621968L;

        private final List<? extends Predicate<? super T>> components;

        @Override
        public boolean test(T t) {
            for (int i = 0; i < components.size(); i++) {
                if (components.get(i).test(t)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return components.hashCode() + 0x053c91cf;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof OrPredicate) {
                OrPredicate<?> that = (OrPredicate<?>) obj;
                return components.equals(that.components);
            }
            return false;
        }

        @Override
        public String toString() {
            return toStringHelper("or", components);
        }
    }

    @AllArgsConstructor
    private static class AndPredicate<T> implements Predicate<T>, Serializable {
        private static final long serialVersionUID = 5871026697003815533L;

        private final List<? extends Predicate<? super T>> components;

        @Override
        public boolean test(T t) {
            for (int i = 0; i < components.size(); i++) {
                if (!components.get(i).test(t)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return components.hashCode() + 0x12472c2c;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AndPredicate) {
                AndPredicate<?> that = (AndPredicate<?>) obj;
                return components.equals(that.components);
            }
            return false;
        }

        @Override
        public String toString() {
            return toStringHelper("and", components);
        }
    }

    private static String toStringHelper(String methodName, Iterable<?> components) {
        StringBuilder builder = new StringBuilder("").append(methodName).append('(');
        boolean first = true;
        for (Object o : components) {
            if (!first) {
                builder.append(',');
            }
            builder.append(o);
            first = false;
        }
        return builder.append(')').toString();
    }
}
