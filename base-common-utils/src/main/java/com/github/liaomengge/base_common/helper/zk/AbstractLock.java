package com.github.liaomengge.base_common.helper.zk;

import com.github.liaomengge.base_common.utils.json.LyJacksonUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import com.github.liaomengge.base_common.utils.thread.LyThreadFactoryBuilderUtil;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 5/27/16.
 * 基于Zookeeper的分布式锁
 */
public abstract class AbstractLock {

    protected final Logger log = LyLogger.getInstance(AbstractLock.class);

    private int lockNumber = 1; //允许获取的锁数量(默认为1,即最小节点=自身时, 认为获得锁)
    private ZkClient zk = null;
    private String rootNode = "/base"; //根节点名称
    private String selfNode;
    private final String className = this.getClass().getSimpleName(); //当前实例的className
    private String selfNodeName;//自身注册的临时节点名
    private boolean handling = false;
    private static final String SPLIT = "/";
    private String selfNodeFullName;

    /**
     * 通过Zk获取分布式锁
     * 传统spring版
     */
    protected void getLock(int lockNumber, String rootNode) {
        this.rootNode = rootNode;
        initBean();
        setLockNumber(lockNumber);
        initNode();
        subscribe();
        register();
        heartBeat();
        keepRunning();
    }

    protected void getLock(int lockNumber) {
        getLock(lockNumber, rootNode);
    }

    protected void getLock(String rootNode) {
        getLock(1, rootNode);
    }


    protected void getLock() {
        getLock(1, rootNode);
    }

    /**
     * 通过Zk获取分布式锁
     * spring boot版
     *
     * @param lockNumber
     * @param rootNode
     */
    protected void getLockBoot(int lockNumber, String rootNode) {
        this.rootNode = rootNode;
        initBean();
        setLockNumber(lockNumber);
        initNode();
        subscribe();
        register();
        heartBeat();
    }

    protected void getLockBoot(int lockNumber) {
        getLockBoot(lockNumber, rootNode);
    }

    protected void getLockBoot(String rootNode) {
        getLockBoot(1, rootNode);
    }


    protected void getLockBoot() {
        getLockBoot(1, rootNode);
    }

    /**
     * 初始化结点
     */
    private void initNode() {

        String error;
        if (!rootNode.startsWith(SPLIT)) {
            error = "rootNode必须以" + SPLIT + "开头";
            log.error(error);
            throw new RuntimeException(error);
        }

        if (rootNode.endsWith(SPLIT)) {
            error = "不能以" + SPLIT + "结尾";
            log.error(error);
            throw new RuntimeException(error);
        }

        int start = 1;
        int index = rootNode.indexOf(SPLIT, start);
        String path;
        while (index != -1) {
            path = rootNode.substring(0, index);
            if (!zk.exists(path)) {
                zk.createPersistent(path);
            }
            start = index + 1;
            if (start >= rootNode.length()) {
                break;
            }
            index = rootNode.indexOf(SPLIT, start);
        }

        if (start < rootNode.length()) {
            if (!zk.exists(rootNode)) {
                zk.createPersistent(rootNode);
            }
        }

        selfNode = rootNode + SPLIT + className;

        if (!zk.exists(selfNode)) {
            zk.createPersistent(selfNode);
        }
    }

    /**
     * 向zk注册自身节点
     */
    private void register() {
        selfNodeName = zk.createEphemeralSequential(selfNode + SPLIT, StringUtils.EMPTY);
        if (!StringUtils.isEmpty(selfNodeName)) {
            selfNodeFullName = selfNodeName;
            log.info("自身节点：" + selfNodeName + ",注册成功！");
            selfNodeName = selfNodeName.substring(selfNode.length() + 1);
        }
        checkMin();
    }

    /**
     * 订阅zk的节点变化
     */
    private void subscribe() {
        zk.subscribeChildChanges(selfNode, (parentPath, currentChilds) -> {
            checkMin();
        });
    }

    /**
     * 检测是否获得锁
     */
    private synchronized void checkMin() {
        List<String> list = zk.getChildren(selfNode);
        if (CollectionUtils.isEmpty(list)) {
            log.error(selfNode + " 无任何子节点!");
            lockFail();
            handling = false;
            return;
        }
        //按序号从小到大排
        Collections.sort(list);

        //如果自身ID在前N个锁中, 则认为获取成功
        int max = Math.min(getLockNumber(), list.size());
        for (int i = 0; i < max; i++) {
            if (list.get(i).equals(selfNodeName)) {
                if (!handling) {
                    handling = true;
                    log.info("获得锁成功！");
                    lockSuccess();
                }
                return;
            }
        }

        int selfIndex = list.indexOf(selfNodeName);
        if (selfIndex > 0) {
            log.info("前面还有节点" + list.get(selfIndex - 1) + ", 获取锁失败！");
        } else {
            log.info("获取锁失败！");
        }
        lockFail();

        handling = false;
    }

    /**
     * 获得锁成功的处理回调
     */
    protected abstract void lockSuccess();

    /**
     * 获得锁失败的处理回调
     */
    protected void lockFail() {
    }

    /**
     * 初始化相关的Bean对象
     */
    protected abstract void initBean();


    protected void setZkClient(ZkClient zk) {
        this.zk = zk;
    }

    protected int getLockNumber() {
        return lockNumber;
    }

    protected void setLockNumber(int lockNumber) {
        this.lockNumber = lockNumber;
    }

    protected void setRootNode(String value) {
        this.rootNode = value;
    }

    /**
     * 防程序退出
     */
    protected void keepRunning() {
        byte[] lock = new byte[0];
        try {
            synchronized (lock) {
                while (true) {
                    lock.wait();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Keep Running出错, 程序已退出", e);
        }
    }

    /**
     * 定时向zk发送心跳
     */
    private void heartBeat() {
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1, LyThreadFactoryBuilderUtil.build("heart" +
                "-zk"));
        service.scheduleAtFixedRate(() -> {
            HeartBeat heartBeat = new HeartBeat();
            heartBeat.setHostIp(LyNetworkUtil.getHostAddress());
            heartBeat.setHostName(LyNetworkUtil.getHostName());
            heartBeat.setLastTime(new Date());
            zk.writeData(selfNodeFullName, LyJacksonUtil.toJson(heartBeat));
        }, 0, 15, TimeUnit.SECONDS);
    }
}
