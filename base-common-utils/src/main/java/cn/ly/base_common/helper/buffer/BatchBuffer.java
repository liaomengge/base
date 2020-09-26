package cn.ly.base_common.helper.buffer;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Uninterruptibles;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by liaomengge on 2020/9/8.
 */
public class BatchBuffer<T> {

    private final int queueSize;
    private int batchSize;
    private QueueStrategy queueStrategy;
    private BlockingQueue<T> blockingQueue;

    @Getter
    @AllArgsConstructor
    public static class QueueStrategy {
        private Duration waitTime;
    }

    public BatchBuffer(int queueSize) {
        this.queueSize = queueSize;
        this.blockingQueue = new LinkedBlockingQueue<>(this.queueSize);
    }

    public BatchBuffer(int queueSize, QueueStrategy queueStrategy) {
        this.queueSize = queueSize;
        this.queueStrategy = queueStrategy;
        this.blockingQueue = new LinkedBlockingQueue<>(this.queueSize);
    }

    public BatchBuffer(int queueSize, int batchSize, QueueStrategy queueStrategy) {
        this.queueSize = queueSize;
        this.batchSize = batchSize;
        this.queueStrategy = queueStrategy;
        this.blockingQueue = new LinkedBlockingQueue<>(this.queueSize);
    }

    public void produce(T t, Consumer<T> consumer) {
        boolean status;
        try {
            status = this.blockingQueue.offer(t);
        } catch (Exception e) {
            status = false;
        }
        if (!status) {
            consumer.accept(t);
        }
    }

    public void consume(Consumer<List<T>> consumer) {
        consume(consumer, this.batchSize, this.queueStrategy);
    }

    public void consume(Consumer<List<T>> consumer, int batchSize) {
        consume(consumer, batchSize, this.queueStrategy);
    }

    public void consume(Consumer<List<T>> consumer, QueueStrategy queueStrategy) {
        consume(consumer, this.batchSize, queueStrategy);
    }

    public void consume(Consumer<List<T>> consumer, int batchSize, QueueStrategy queueStrategy) {
        int bufferSize;
        List<T> buffer = Lists.newArrayListWithCapacity(batchSize);
        Duration waitTime = queueStrategy.getWaitTime();
        if (Objects.isNull(waitTime)) {
            bufferSize = this.blockingQueue.drainTo(buffer, batchSize);
        } else {
            bufferSize = Queues.drainUninterruptibly(this.blockingQueue, buffer, batchSize, waitTime);
        }
        if (bufferSize == 0 && buffer.size() == 0) {
            buffer.add(Uninterruptibles.takeUninterruptibly(this.blockingQueue));
        }
        consumer.accept(buffer);
    }
}
