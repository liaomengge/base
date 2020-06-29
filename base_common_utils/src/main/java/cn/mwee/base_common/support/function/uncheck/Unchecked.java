package cn.mwee.base_common.support.function.uncheck;

import cn.mwee.base_common.support.function.checked.*;
import cn.mwee.base_common.utils.error.MwExceptionUtil;
import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * https://segmentfault.com/a/1190000007832130
 * Created by liaomengge on 2019/10/31.
 */
@UtilityClass
public class Unchecked {

    public <T, R> Function<T, R> function(CheckedFunction<T, R> mapper) {
        Objects.requireNonNull(mapper);
        return t -> {
            try {
                return mapper.apply(t);
            } catch (Throwable e) {
                throw MwExceptionUtil.unchecked(e);
            }
        };
    }

    public <T> Consumer<T> consumer(CheckedConsumer<T> mapper) {
        Objects.requireNonNull(mapper);
        return t -> {
            try {
                mapper.accept(t);
            } catch (Throwable e) {
                throw MwExceptionUtil.unchecked(e);
            }
        };
    }

    public <T> Supplier<T> supplier(CheckedSupplier<T> mapper) {
        Objects.requireNonNull(mapper);
        return () -> {
            try {
                return mapper.get();
            } catch (Throwable e) {
                throw MwExceptionUtil.unchecked(e);
            }
        };
    }

    public Runnable runnable(CheckedRunnable runnable) {
        Objects.requireNonNull(runnable);
        return () -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                throw MwExceptionUtil.unchecked(e);
            }
        };
    }

    public <T> Callable<T> callable(CheckedCallable<T> callable) {
        Objects.requireNonNull(callable);
        return () -> {
            try {
                return callable.call();
            } catch (Throwable e) {
                throw MwExceptionUtil.unchecked(e);
            }
        };
    }

    public <T> Comparator<T> comparator(CheckedComparator<T> comparator) {
        Objects.requireNonNull(comparator);
        return (T o1, T o2) -> {
            try {
                return comparator.compare(o1, o2);
            } catch (Throwable e) {
                throw MwExceptionUtil.unchecked(e);
            }
        };
    }

    /********************************华丽的分割线********************************/

    public <T, R> Function<T, R> apply(CheckedFunction<T, R> mapper, Function<Throwable, R> handler) {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(handler);
        return t -> {
            try {
                return mapper.apply(t);
            } catch (Throwable e) {
                return handler.apply(e);
            }
        };
    }

    public <T> Consumer<T> accept(CheckedConsumer<T> mapper, Consumer<Throwable> handler) {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(handler);
        return t -> {
            try {
                mapper.accept(t);
            } catch (Throwable e) {
                handler.accept(e);
            }
        };
    }
}
