package com.github.liaomengge.base_common.utils.concurrent;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2022/03/04.
 */
@UtilityClass
public class LyFutureUtil {

    public <T> void asyncExec(List<T> list, Runnable runnable, Consumer<Exception> exceptionConsumer,
                              ExecutorService executorService) {
        List<Future<?>> futureList =
                list.stream().map(t -> executorService.submit(runnable)).collect(Collectors.toList());
        futureList.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                exceptionConsumer.accept(e);
            }
        });
    }

    public <T> void asyncExec(List<T> list, Consumer<T> consumer, Consumer<Exception> exceptionConsumer,
                              ExecutorService executorService) {
        List<Future<?>> futureList =
                list.stream().map(t -> executorService.submit(() -> consumer.accept(t))).collect(Collectors.toList());
        futureList.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                exceptionConsumer.accept(e);
            }
        });
    }

    public <T, R> List<R> asyncExec(List<T> list, Supplier<R> supplier, Function<Exception, R> rFunction,
                                    ExecutorService executorService) {
        List<Future<R>> futureList =
                list.stream().map(t -> executorService.submit(() -> supplier.get())).collect(Collectors.toList());
        return futureList.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                return rFunction.apply(e);
            }
        }).collect(Collectors.toList());
    }

    public <T, R> List<R> asyncExec(List<T> list, Function<T, R> function, Function<Exception, R> rFunction,
                                    ExecutorService executorService) {
        List<Future<R>> futureList =
                list.stream().map(t -> executorService.submit(() -> function.apply(t))).collect(Collectors.toList());
        return futureList.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                return rFunction.apply(e);
            }
        }).collect(Collectors.toList());
    }
}
