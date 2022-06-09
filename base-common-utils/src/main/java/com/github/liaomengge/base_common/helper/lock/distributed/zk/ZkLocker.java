package com.github.liaomengge.base_common.helper.lock.distributed.zk;

import com.github.liaomengge.base_common.helper.lock.DistributedLocker;
import com.github.liaomengge.base_common.helper.lock.distributed.callback.AcquiredLockCallback;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.github.liaomengge.base_common.helper.lock.distributed.consts.DistributedConst.DEFAULT_TIME_UNIT;
import static com.github.liaomengge.base_common.helper.lock.distributed.consts.DistributedConst.ZK_LOCKER_PREFIX;

/**
 * Created by liaomengge on 17/12/19.
 */
public class ZkLocker implements DistributedLocker {

    private final CuratorFrameworkManager curatorFrameworkManager;

    public ZkLocker(CuratorFrameworkManager curatorFrameworkManager) {
        this.curatorFrameworkManager = curatorFrameworkManager;
    }

    /**
     * 获取锁
     *
     * @param path
     * @throws Exception
     */
    public void lock(String path) throws Exception {
        CuratorFramework curatorFramework = curatorFrameworkManager.getCuratorFramework();
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, ZK_LOCKER_PREFIX + path);
        mutex.acquire();
    }

    /**
     * 尝试获取锁
     *
     * @param path
     * @param time
     * @param timeUnit
     * @return
     * @throws Exception
     */
    public boolean tryLock(String path, long time, TimeUnit timeUnit) throws Exception {
        CuratorFramework curatorFramework = curatorFrameworkManager.getCuratorFramework();
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, ZK_LOCKER_PREFIX + path);
        return mutex.acquire(time, timeUnit);
    }

    /**
     * 释放锁
     *
     * @param path
     * @throws Exception
     */
    public void unlock(String path) throws Exception {
        CuratorFramework curatorFramework = curatorFrameworkManager.getCuratorFramework();
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, ZK_LOCKER_PREFIX + path);
        if (Objects.nonNull(mutex) && mutex.isOwnedByCurrentThread()) {
            mutex.release();
        }
    }

    /**
     * 使用分布式锁
     * 锁不可用时,将一直等待
     *
     * @param path
     * @param callback
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T lock(String path, AcquiredLockCallback<T> callback) throws Exception {
        CuratorFramework curatorFramework = curatorFrameworkManager.getCuratorFramework();
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, ZK_LOCKER_PREFIX + path);
        try {
            try {
                mutex.acquire();
            } catch (Exception e) {
                return callback.onFailure(e);
            }
            return callback.onSuccess();
        } finally {
            if (Objects.nonNull(mutex) && mutex.isOwnedByCurrentThread()) {
                mutex.release();
            }
        }
    }

    /**
     * 尝试获取锁,不等待
     *
     * @param path
     * @param callback
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T tryLock(String path, AcquiredLockCallback<T> callback) throws Exception {
        return this.tryLock(path, callback, 0L, DEFAULT_TIME_UNIT);
    }

    public <T> T tryLock(String path, AcquiredLockCallback<T> callback, long time) throws Exception {
        return this.tryLock(path, callback, time, DEFAULT_TIME_UNIT);
    }

    /**
     * 使用分布式锁
     * 锁不可用时,指定锁等待时间
     *
     * @param path
     * @param callback
     * @param time
     * @param timeUnit
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T tryLock(String path, AcquiredLockCallback<T> callback, long time, TimeUnit timeUnit) throws Exception {
        CuratorFramework curatorFramework = curatorFrameworkManager.getCuratorFramework();
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, ZK_LOCKER_PREFIX + path);
        boolean isSuccess;
        try {
            isSuccess = mutex.acquire(time, timeUnit);
        } catch (Exception e) {
            return callback.onFailure(e);
        }
        if (isSuccess) {
            try {
                return callback.onSuccess();
            } finally {
                if (Objects.nonNull(mutex) && mutex.isOwnedByCurrentThread()) {
                    mutex.release();
                }
            }
        }
        return callback.onFailure();
    }

}
