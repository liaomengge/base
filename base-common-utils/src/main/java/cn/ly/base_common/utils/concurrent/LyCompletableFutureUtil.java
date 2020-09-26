package cn.ly.base_common.utils.concurrent;

import static java.util.stream.Collectors.toList;

import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;
import net.javacrumbs.futureconverter.java8guava.FutureConverter;

/**
 * CompletableFuture异步执行主线程(tomcat线程)不参与执行
 * parallelStream并行流计算,主线程(tomcat线程)参与计算
 *
 * Created by liaomengge on 17/12/6.
 */
@UtilityClass
public class LyCompletableFutureUtil {

    public <T> void asyncExec(List<T> list, Runnable runnable) {
        list.stream().map(t -> CompletableFuture.runAsync(runnable)).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
    }

    public <T> void asyncExec(List<T> list, Runnable runnable, Executor supplyExecutor) {
        list.stream().map(t -> CompletableFuture.runAsync(runnable, supplyExecutor)).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
    }

    public <T> void asyncExec(List<T> list, Consumer<T> consumer) {
        list.stream().map(t -> CompletableFuture.runAsync(() -> consumer.accept(t))).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
    }

    public <T> void asyncExec(List<T> list, Consumer<T> consumer, Executor supplyExecutor) {
        list.stream().map(t -> CompletableFuture.runAsync(() -> consumer.accept(t), supplyExecutor)).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
    }

    public <T> List<T> asyncExec(List<T> list, Supplier<T> supplier) {
        return list.stream().map(t -> CompletableFuture.supplyAsync(supplier)).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
    }

    public <T> List<T> asyncExec(List<T> list, Supplier<T> supplier, Executor supplyExecutor) {
        return list.stream().map(t -> CompletableFuture.supplyAsync(supplier, supplyExecutor)).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
    }

    public <T, R> List<R> asyncExec(List<T> list, Function<T, R> function) {
        return list.stream().map(t -> CompletableFuture.supplyAsync(() -> function.apply(t))).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
    }

    public <T, R> List<R> asyncExec(List<T> list, Function<T, R> function, Executor supplyExecutor) {
        return list.stream().map(t -> CompletableFuture.supplyAsync(() -> function.apply(t), supplyExecutor)).collect(toList())
                .stream().map(CompletableFuture::join).collect(toList());
    }

    public <T> List<T> asyncExec(List<T> list, Supplier<T> supplier, Function<Throwable, T> tFuction) {
        return list.stream().map(t -> CompletableFuture.supplyAsync(supplier).exceptionally(tFuction)).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
    }

    public <T> List<T> asyncExec(List<T> list, Supplier<T> supplier, Function<Throwable, T> tFuction,
                                 Executor supplyExecutor) {
        return list.stream().map(t -> CompletableFuture.supplyAsync(supplier, supplyExecutor).exceptionally(tFuction)).collect
                (toList()).stream().map(CompletableFuture::join).collect(toList());
    }

    public <T, R> List<R> asyncExec(List<T> list, Function<T, R> function, Function<Throwable, R> tFuction) {
        return list.stream().map(t -> CompletableFuture.supplyAsync(() -> function.apply(t)).exceptionally(tFuction)).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
    }

    public <T, R> List<R> asyncExec(List<T> list, Function<T, R> function, Function<Throwable, R> tFuction,
                                    Executor supplyExecutor) {
        return list.stream().map(t -> CompletableFuture.supplyAsync(() -> function.apply(t), supplyExecutor).exceptionally(tFuction)).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
    }

    /************************************************华丽的分割线(并行流)************************************************/

    public <T, R> List<R> asyncExec2(List<T> list, Function<T, R> function) {
        return list.parallelStream().map(function).collect(toList());
    }

    public <T, R> List<R> asyncExec2(List<T> list, Predicate<T> predicate, Function<T, R> function) {
        return list.parallelStream().filter(predicate).map(function).collect(toList());
    }

    /************************************************华丽的分割线(补充)**************************************************/

    /**
     * 任何一个fail,则停止所有completableFuture
     *
     * @param futures
     * @return
     */
    public CompletableFuture<?> anyOfFail(CompletableFuture<?>... futures) {
        // 首先构造一个当全部成功则成功的CompletableFuture
        CompletableFuture<Void> allComplete = CompletableFuture.allOf(futures);

        // 再构造一个当有一个失败则失败的的CompletableFuture
        CompletableFuture<?> anyException = new CompletableFuture<>();
        for (CompletableFuture<?> future : futures) {
            future.exceptionally((Throwable t) -> {
                //对于传入的futures列表, 如果一个有异常, 则把新建的CompletableFuture置为成功
                anyException.completeExceptionally(t);
                return null;
            });
        }

        // 让allComplete和anyException其中有一个完成则完成
        // 如果allComplete有一个异常, anyException会成功完成, 则整个就提前完成了
        return CompletableFuture.anyOf(allComplete, anyException);
    }

    /********************************************华丽的分割线(guava/jdk8转换)********************************************/

    public <T> ListenableFuture<T> toListenableFuture(Future<T> future) {
        return JdkFutureAdapters.listenInPoolThread(future);
    }

    public <T> ListenableFuture<T> toListenableFuture(Future<T> future, Executor executor) {
        return JdkFutureAdapters.listenInPoolThread(future, executor);
    }

    public <T> ListenableFuture<T> toListenableFuture(CompletableFuture<T> future) {
        return FutureConverter.toListenableFuture(future);
    }

    public <T> CompletableFuture<T> toCompletableFuture(ListenableFuture<T> listenableFuture) {
        return FutureConverter.toCompletableFuture(listenableFuture);
    }

    public <T> CompletableFuture<T> toCompletable(Future<T> future, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Uninterruptibles.getUninterruptibly(future);
            } catch (ExecutionException e) {
                throw new CompletionException(e);
            }
        }, executor);
    }
}
