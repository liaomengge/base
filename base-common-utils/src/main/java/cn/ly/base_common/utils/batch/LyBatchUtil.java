package cn.ly.base_common.utils.batch;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 18/5/10.
 */
@UtilityClass
public class LyBatchUtil {

    public <T> void process(@NonNull List<T> list, int batchSize, Consumer<List<T>> consumer) {
        List<T> subList;
        while (!list.isEmpty()) {
            subList = list.subList(0, Math.min(batchSize, list.size()));
            consumer.accept(subList);
            subList.clear();
        }
    }

    public <T, R> void process(@NonNull List<T> list, int batchSize, Function<List<T>, R> function,
                               Consumer<R> consumer) {
        List<T> subList;
        while (!list.isEmpty()) {
            subList = list.subList(0, Math.min(batchSize, list.size()));
            R result = function.apply(subList);
            consumer.accept(result);
            subList.clear();
        }
    }

    public <T> void process2(@NonNull List<T> list, int batchSize, Consumer<List<T>> consumer) {
        List<List<T>> partitionList = Lists.partition(list, batchSize);
        partitionList.forEach(consumer);
    }

    public <T> Stream<List<T>> process2(@NonNull List<T> list, int batchSize) {
        List<List<T>> partitionList = Lists.partition(list, batchSize);
        return partitionList.stream();
    }

    public <T, R> Stream<R> process2(@NonNull List<T> list, int batchSize, Function<List<T>, R> function) {
        return process2(list, batchSize).map(function);
    }

    public <T, R> void process2(@NonNull List<T> list, int batchSize, Function<List<T>, R> function,
                                Consumer<R> consumer) {
        process2(list, batchSize, function).forEach(consumer);
    }

    public <T, V> Stream<List<V>> process3(@NonNull List<T> list, int batchSize, Function<List<T>, List<V>>
            function) {
        return process2(list, batchSize).map(function);
    }

    public <T, R, V> V process3(@NonNull List<T> list, int batchSize, Function<List<T>, List<R>>
            function, Collector<R, ?, V> collector) {
        return process3(list, batchSize, function).flatMap(Collection::stream).collect(collector);
    }
}
