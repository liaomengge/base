package cn.ly.base_common.influx.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2020/7/21.
 */
@UtilityClass
public class QueueUtil {

    public <E> int drainUninterruptibly(BlockingQueue<E> q, Collection<? super E> buffer, int numElements,
                                        long timeout, TimeUnit unit) {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        int added = 0;
        boolean interrupted = false;
        try {
            while (added < numElements) {
                added += q.drainTo(buffer, numElements - added);
                if (added < numElements) {
                    E e;
                    while (true) {
                        try {
                            e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
                            break;
                        } catch (InterruptedException ex) {
                            interrupted = true;
                        }
                    }
                    if (e == null) {
                        break;
                    }
                    buffer.add(e);
                    added++;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        return added;
    }
}
