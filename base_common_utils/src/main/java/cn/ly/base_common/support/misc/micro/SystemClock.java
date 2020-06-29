package cn.ly.base_common.support.misc.micro;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 利用ScheduledExecutorService实现高并发场景下System.curentTimeMillis()的性能问题的优化.
 * 高并发场景下System.currentTimeMillis()的性能问题的优化
 * System.currentTimeMillis()的调用比new一个普通对象要耗时的多（具体耗时高出多少我还没测试过, 有人说是100倍左右）
 * System.currentTimeMillis()之所以慢是因为去跟系统打了一次交道
 * 后台定时更新时钟, JVM退出时, 线程自动回收
 * <p>
 * Created by liaomengge on 2019/10/10.
 */
public class SystemClock {

    private final long period;
    private volatile long now;

    /**
     * 构造
     *
     * @param period 时钟更新间隔, 单位毫秒
     */
    private SystemClock(long period) {
        this.period = period;
        this.now = System.currentTimeMillis();
        initialize();
    }

    /**
     * 开启计时器线程
     */
    private void initialize() {
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "system-clock");
            thread.setDaemon(true);
            return thread;
        });
        scheduledExecutor.scheduleAtFixedRate(() -> now = System.currentTimeMillis(), period, period,
                TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (Objects.nonNull(scheduledExecutor)) {
                scheduledExecutor.shutdown();
            }
        }));
    }

    /**
     * @return 当前时间毫秒数
     */
    private long now() {
        return now;
    }

    /**
     * 单例
     *
     * @author Looly
     */
    private static class InstanceHolder {
        public static final SystemClock INSTANCE = new SystemClock(1);
    }

    /**
     * 单例实例
     *
     * @return 单例实例
     */
    private static SystemClock instance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * @return 当前时间
     */
    public static long currentTimeMillis() {
        return instance().now();
    }

    /**
     * @return 当前时间字符串表现形式
     */
    public static String currentTime() {
        return new Timestamp(currentTimeMillis()).toString();
    }
}
