package cn.ly.base_common.utils.concurrent;

import cn.ly.base_common.support.tuple.Tuple2;
import cn.ly.base_common.support.tuple.Tuple3;
import cn.ly.base_common.support.tuple.Tuple4;
import cn.ly.base_common.support.tuple.Tuple5;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * Created by liaomengge on 2020/6/5.
 */
@UtilityClass
public class LyMoreCompletableFutureUtil {

    public <A, B> Tuple2<A, B> asyncExec(Supplier<A> supplier, Supplier<B> supplier2) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2);
        return asyncExec(future, future2);
    }

    public <A, B> Tuple2<A, B> asyncExec(Supplier<A> supplier, Supplier<B> supplier2, Executor executor) {
        return asyncExec(supplier, supplier2, executor, executor);
    }

    public <A, B> Tuple2<A, B> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                         Executor supplyExecutor, Executor combineExecutor) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier, supplyExecutor);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2, supplyExecutor);
        return asyncExec(future, future2, combineExecutor);
    }

    public <A, B> Tuple2<A, B> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                         BiConsumer<A, Throwable> aConsumer,
                                         BiConsumer<B, Throwable> bConsumer) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier).whenCompleteAsync(aConsumer);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2).whenCompleteAsync(bConsumer);
        return asyncExec(future, future2);
    }

    public <A, B> Tuple2<A, B> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                         BiConsumer<A, Throwable> aConsumer,
                                         BiConsumer<B, Throwable> bConsumer,
                                         Executor executor) {
        return asyncExec(supplier, supplier2, aConsumer, bConsumer, executor, executor);
    }

    public <A, B> Tuple2<A, B> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                         BiConsumer<A, Throwable> aConsumer, BiConsumer<B, Throwable> bConsumer,
                                         Executor supplyExecutor, Executor combineExecutor) {
        CompletableFuture future =
                CompletableFuture.supplyAsync(supplier, supplyExecutor).whenCompleteAsync(aConsumer, supplyExecutor);
        CompletableFuture future2 =
                CompletableFuture.supplyAsync(supplier2, supplyExecutor).whenCompleteAsync(bConsumer, supplyExecutor);
        return asyncExec(future, future2, combineExecutor);
    }

    /************************************************华丽的分割线(Tuple3)***********************************************/

    public <A, B, C> Tuple3<A, B, C> asyncExec(Supplier<A> supplier, Supplier<B> supplier2, Supplier<C> supplier3) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2);
        CompletableFuture future3 = CompletableFuture.supplyAsync(supplier3);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple3.of((A) val.get(0), (B) val.get(1), (C) val.get(2))).join();
    }

    public <A, B, C> Tuple3<A, B, C> asyncExec(Supplier<A> supplier, Supplier<B> supplier2, Supplier<C> supplier3,
                                               Executor executor) {
        return asyncExec(supplier, supplier2, supplier3, executor, executor);
    }

    public <A, B, C> Tuple3<A, B, C> asyncExec(Supplier<A> supplier, Supplier<B> supplier2, Supplier<C> supplier3,
                                               Executor supplyExecutor, Executor combineExecutor) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier, supplyExecutor);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2, supplyExecutor);
        CompletableFuture future3 = CompletableFuture.supplyAsync(supplier3, supplyExecutor);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple3.of((A) val.get(0), (B) val.get(1), (C) val.get(2)), combineExecutor).join();
    }

    public <A, B, C> Tuple3<A, B, C> asyncExec(Supplier<A> supplier, Supplier<B> supplier2, Supplier<C> supplier3,
                                               BiConsumer<A, Throwable> aConsumer,
                                               BiConsumer<B, Throwable> bConsumer,
                                               BiConsumer<C, Throwable> cConsumer) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier).whenCompleteAsync(aConsumer);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2).whenCompleteAsync(bConsumer);
        CompletableFuture future3 = CompletableFuture.supplyAsync(supplier3).whenCompleteAsync(cConsumer);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple3.of((A) val.get(0), (B) val.get(1), (C) val.get(2))).join();
    }

    public <A, B, C> Tuple3<A, B, C> asyncExec(Supplier<A> supplier, Supplier<B> supplier2, Supplier<C> supplier3,
                                               BiConsumer<A, Throwable> aConsumer,
                                               BiConsumer<B, Throwable> bConsumer,
                                               BiConsumer<C, Throwable> cConsumer,
                                               Executor executor) {
        return asyncExec(supplier, supplier2, supplier3, aConsumer, bConsumer, cConsumer, executor, executor);
    }

    public <A, B, C> Tuple3<A, B, C> asyncExec(Supplier<A> supplier, Supplier<B> supplier2, Supplier<C> supplier3,
                                               BiConsumer<A, Throwable> aConsumer,
                                               BiConsumer<B, Throwable> bConsumer,
                                               BiConsumer<C, Throwable> cConsumer,
                                               Executor supplyExecutor, Executor combineExecutor) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier, supplyExecutor).whenCompleteAsync(aConsumer);
        CompletableFuture future2 =
                CompletableFuture.supplyAsync(supplier2, supplyExecutor).whenCompleteAsync(bConsumer);
        CompletableFuture future3 =
                CompletableFuture.supplyAsync(supplier3, supplyExecutor).whenCompleteAsync(cConsumer);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple3.of((A) val.get(0), (B) val.get(1), (C) val.get(2)), combineExecutor).join();
    }

    /************************************************华丽的分割线(Tuple4)***********************************************/

    public <A, B, C, D> Tuple4<A, B, C, D> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                     Supplier<C> supplier3, Supplier<D> supplier4) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2);
        CompletableFuture future3 = CompletableFuture.supplyAsync(supplier3);
        CompletableFuture future4 = CompletableFuture.supplyAsync(supplier4);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3, future4);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple4.of((A) val.get(0), (B) val.get(1), (C) val.get(2), (D) val.get(3))).join();
    }

    public <A, B, C, D> Tuple4<A, B, C, D> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                     Supplier<C> supplier3, Supplier<D> supplier4,
                                                     Executor executor) {
        return asyncExec(supplier, supplier2, supplier3, supplier4, executor, executor);
    }

    public <A, B, C, D> Tuple4<A, B, C, D> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                     Supplier<C> supplier3, Supplier<D> supplier4,
                                                     Executor supplyExecutor, Executor combineExecutor) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier, supplyExecutor);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2, supplyExecutor);
        CompletableFuture future3 = CompletableFuture.supplyAsync(supplier3, supplyExecutor);
        CompletableFuture future4 = CompletableFuture.supplyAsync(supplier4, supplyExecutor);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3, future4);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple4.of((A) val.get(0), (B) val.get(1), (C) val.get(2), (D) val.get(3)),
                        combineExecutor).join();
    }

    public <A, B, C, D> Tuple4<A, B, C, D> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                     Supplier<C> supplier3, Supplier<D> supplier4,
                                                     BiConsumer<A, Throwable> aConsumer,
                                                     BiConsumer<B, Throwable> bConsumer,
                                                     BiConsumer<C, Throwable> cConsumer,
                                                     BiConsumer<D, Throwable> dConsumer) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier).whenCompleteAsync(aConsumer);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2).whenCompleteAsync(bConsumer);
        CompletableFuture future3 = CompletableFuture.supplyAsync(supplier3).whenCompleteAsync(cConsumer);
        CompletableFuture future4 = CompletableFuture.supplyAsync(supplier4).whenCompleteAsync(dConsumer);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3, future4);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple4.of((A) val.get(0), (B) val.get(1), (C) val.get(2), (D) val.get(3))).join();
    }

    public <A, B, C, D> Tuple4<A, B, C, D> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                     Supplier<C> supplier3, Supplier<D> supplier4,
                                                     BiConsumer<A, Throwable> aConsumer,
                                                     BiConsumer<B, Throwable> bConsumer,
                                                     BiConsumer<C, Throwable> cConsumer,
                                                     BiConsumer<D, Throwable> dConsumer,
                                                     Executor executor) {
        return asyncExec(supplier, supplier2, supplier3, supplier4, aConsumer, bConsumer, cConsumer, dConsumer,
                executor, executor);
    }

    public <A, B, C, D> Tuple4<A, B, C, D> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                     Supplier<C> supplier3, Supplier<D> supplier4,
                                                     BiConsumer<A, Throwable> aConsumer,
                                                     BiConsumer<B, Throwable> bConsumer,
                                                     BiConsumer<C, Throwable> cConsumer,
                                                     BiConsumer<D, Throwable> dConsumer,
                                                     Executor supplyExecutor, Executor combineExecutor) {
        CompletableFuture future =
                CompletableFuture.supplyAsync(supplier, supplyExecutor).whenCompleteAsync(aConsumer);
        CompletableFuture future2 =
                CompletableFuture.supplyAsync(supplier2, supplyExecutor).whenCompleteAsync(bConsumer);
        CompletableFuture future3 =
                CompletableFuture.supplyAsync(supplier3, supplyExecutor).whenCompleteAsync(cConsumer);
        CompletableFuture future4 =
                CompletableFuture.supplyAsync(supplier4, supplyExecutor).whenCompleteAsync(dConsumer);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3, future4);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple4.of((A) val.get(0), (B) val.get(1), (C) val.get(2), (D) val.get(3)),
                        combineExecutor).join();
    }

    /************************************************华丽的分割线(Tuple5)***********************************************/

    public <A, B, C, D, E> Tuple5<A, B, C, D, E> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                           Supplier<C> supplier3, Supplier<D> supplier4,
                                                           Supplier<E> supplier5) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2);
        CompletableFuture future3 = CompletableFuture.supplyAsync(supplier3);
        CompletableFuture future4 = CompletableFuture.supplyAsync(supplier4);
        CompletableFuture future5 = CompletableFuture.supplyAsync(supplier5);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3, future4, future5);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple5.of((A) val.get(0), (B) val.get(1), (C) val.get(2), (D) val.get(3),
                        (E) val.get(4))).join();
    }

    public <A, B, C, D, E> Tuple5<A, B, C, D, E> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                           Supplier<C> supplier3, Supplier<D> supplier4,
                                                           Supplier<E> supplier5,
                                                           Executor executor) {
        return asyncExec(supplier, supplier2, supplier3, supplier4, supplier5, executor, executor);
    }

    public <A, B, C, D, E> Tuple5<A, B, C, D, E> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                           Supplier<C> supplier3, Supplier<D> supplier4,
                                                           Supplier<E> supplier5,
                                                           Executor supplyExecutor, Executor combineExecutor) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier, supplyExecutor);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2, supplyExecutor);
        CompletableFuture future3 = CompletableFuture.supplyAsync(supplier3, supplyExecutor);
        CompletableFuture future4 = CompletableFuture.supplyAsync(supplier4, supplyExecutor);
        CompletableFuture future5 = CompletableFuture.supplyAsync(supplier5, supplyExecutor);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3, future4, future5);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple5.of((A) val.get(0), (B) val.get(1), (C) val.get(2), (D) val.get(3),
                        (E) val.get(4)), combineExecutor).join();
    }

    public <A, B, C, D, E> Tuple5<A, B, C, D, E> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                           Supplier<C> supplier3, Supplier<D> supplier4,
                                                           Supplier<E> supplier5,
                                                           BiConsumer<A, Throwable> aConsumer,
                                                           BiConsumer<B, Throwable> bConsumer,
                                                           BiConsumer<C, Throwable> cConsumer,
                                                           BiConsumer<D, Throwable> dConsumer,
                                                           BiConsumer<E, Throwable> eConsumer) {
        CompletableFuture future = CompletableFuture.supplyAsync(supplier).whenCompleteAsync(aConsumer);
        CompletableFuture future2 = CompletableFuture.supplyAsync(supplier2).whenCompleteAsync(bConsumer);
        CompletableFuture future3 = CompletableFuture.supplyAsync(supplier3).whenCompleteAsync(cConsumer);
        CompletableFuture future4 = CompletableFuture.supplyAsync(supplier4).whenCompleteAsync(dConsumer);
        CompletableFuture future5 = CompletableFuture.supplyAsync(supplier5).whenCompleteAsync(eConsumer);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3, future4, future5);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple5.of((A) val.get(0), (B) val.get(1), (C) val.get(2), (D) val.get(3),
                        (E) val.get(4))).join();
    }

    public <A, B, C, D, E> Tuple5<A, B, C, D, E> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                           Supplier<C> supplier3, Supplier<D> supplier4,
                                                           Supplier<E> supplier5,
                                                           BiConsumer<A, Throwable> aConsumer,
                                                           BiConsumer<B, Throwable> bConsumer,
                                                           BiConsumer<C, Throwable> cConsumer,
                                                           BiConsumer<D, Throwable> dConsumer,
                                                           BiConsumer<E, Throwable> eConsumer,
                                                           Executor executor) {
        return asyncExec(supplier, supplier2, supplier3, supplier4, supplier5, aConsumer, bConsumer, cConsumer,
                dConsumer, eConsumer, executor, executor);
    }

    public <A, B, C, D, E> Tuple5<A, B, C, D, E> asyncExec(Supplier<A> supplier, Supplier<B> supplier2,
                                                           Supplier<C> supplier3, Supplier<D> supplier4,
                                                           Supplier<E> supplier5,
                                                           BiConsumer<A, Throwable> aConsumer,
                                                           BiConsumer<B, Throwable> bConsumer,
                                                           BiConsumer<C, Throwable> cConsumer,
                                                           BiConsumer<D, Throwable> dConsumer,
                                                           BiConsumer<E, Throwable> eConsumer,
                                                           Executor supplyExecutor, Executor combineExecutor) {
        CompletableFuture future =
                CompletableFuture.supplyAsync(supplier, supplyExecutor).whenCompleteAsync(aConsumer);
        CompletableFuture future2 =
                CompletableFuture.supplyAsync(supplier2, supplyExecutor).whenCompleteAsync(bConsumer);
        CompletableFuture future3 =
                CompletableFuture.supplyAsync(supplier3, supplyExecutor).whenCompleteAsync(cConsumer);
        CompletableFuture future4 =
                CompletableFuture.supplyAsync(supplier4, supplyExecutor).whenCompleteAsync(dConsumer);
        CompletableFuture future5 =
                CompletableFuture.supplyAsync(supplier5, supplyExecutor).whenCompleteAsync(eConsumer);
        List<CompletableFuture<?>> completableFutureList = Arrays.asList(future, future2, future3, future4, future5);
        return asList(completableFutureList)
                .thenApplyAsync(val -> Tuple5.of((A) val.get(0), (B) val.get(1), (C) val.get(2), (D) val.get(3),
                        (E) val.get(4)), combineExecutor).join();
    }

    /************************************************华丽的分割线(结果集转换)********************************************/

    /**
     * 无参结果
     *
     * @param futures
     * @param <T>
     */
    public <T> void allof(CompletableFuture<? extends T>... futures) {
        CompletableFuture.allOf(futures).join();
    }

    public <T> void allof(List<CompletableFuture<? extends T>> futures) {
        CompletableFuture.allOf(futures.stream().toArray(CompletableFuture[]::new)).join();
    }

    /**
     * list结果集
     *
     * @param futures
     * @param <T>
     * @return
     */
    public <T> CompletableFuture<List<?>> asList(CompletableFuture<? extends T>... futures) {
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures);
        return allDoneFuture.thenApply(v -> Arrays.stream(futures).map(CompletableFuture::join).collect(toList()));
    }

    public <T> CompletableFuture<List<?>> asList(List<CompletableFuture<? extends T>> futures) {
        return asList(futures.toArray(new CompletableFuture[futures.size()]));
    }

    /************************************************华丽的分割线(并行聚合结果)*******************************************/

    public <A, B> Tuple2<A, B> asyncExec(CompletableFuture<A> future, CompletableFuture<B> future2) {
        return thenCombineAsync(future, future2).join();
    }

    public <A, B> Tuple2<A, B> asyncExec(CompletableFuture<A> future, CompletableFuture<B> future2,
                                         Executor combineExecutor) {
        return thenCombineAsync(future, future2, combineExecutor).join();
    }

    /************************************************华丽的分割线*******************************************************/

    public <A, B> CompletableFuture<Tuple2<A, B>> thenCombineAsync(CompletableFuture<A> future,
                                                                   CompletableFuture<B> future2) {
        return future.thenCombineAsync(future2, (t, r) -> Tuple2.of(t, r));
    }

    public <A, B> CompletableFuture<Tuple2<A, B>> thenCombineAsync(CompletableFuture<A> future,
                                                                   CompletableFuture<B> future2,
                                                                   Executor combineExecutor) {
        return future.thenCombineAsync(future2, (t, r) -> Tuple2.of(t, r), combineExecutor);
    }

    /************************************************华丽的分割线*******************************************************/

    public interface Function3<A, B, C, D> {
        D apply(A a, B b, C c);
    }

    public interface Function4<A, B, C, D, E> {
        E apply(A a, B b, C c, D d);
    }

    public interface Function5<A, B, C, D, E, F> {
        F apply(A a, B b, C c, D d, E e);
    }
}
