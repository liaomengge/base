package cn.mwee.base_common.support.collections;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by liaomengge on 2020/1/7.
 */
@UtilityClass
public class _Collections {

    public <T> void forEach(Collection<T> collection, Consumer<T> doConsumer,
                            BiConsumer<T, Throwable> throwableBiConsumer) {
        collection.forEach(val -> {
            try {
                doConsumer.accept(val);
            } catch (Exception t) {
                throwableBiConsumer.accept(val, t);
            }
        });
    }

    public <T> void forEach(Collection<T> collection, Consumer<T> doConsumer,
                            Predicate<T> continuePredicate) {
        for (T val : collection) {
            if (continuePredicate.test(val)) {
                continue;
            }
            doConsumer.accept(val);
        }
    }

    public <T> void forEach(Collection<T> collection, Consumer<T> doConsumer,
                            Consumer<T> continueConsumer, Predicate<T> continuePredicate) {
        for (T val : collection) {
            if (continuePredicate.test(val)) {
                continueConsumer.accept(val);
                continue;
            }
            doConsumer.accept(val);
        }
    }

    public <T> void forEach(Collection<T> collection, Consumer<T> doConsumer,
                            Predicate<T> continuePredicate, BiConsumer<T, Throwable> throwableBiConsumer) {
        for (T val : collection) {
            if (continuePredicate.test(val)) {
                continue;
            }
            try {
                doConsumer.accept(val);
            } catch (Throwable t) {
                throwableBiConsumer.accept(val, t);
            }
        }
    }

    public <T> void forEach(Collection<T> collection, Consumer<T> doConsumer,
                            Consumer<T> continueConsumer, Predicate<T> continuePredicate,
                            BiConsumer<T, Throwable> throwableBiConsumers) {
        for (T val : collection) {
            if (continuePredicate.test(val)) {
                continueConsumer.accept(val);
                continue;
            }
            try {
                doConsumer.accept(val);
            } catch (Throwable t) {
                throwableBiConsumers.accept(val, t);
            }
        }
    }
}
