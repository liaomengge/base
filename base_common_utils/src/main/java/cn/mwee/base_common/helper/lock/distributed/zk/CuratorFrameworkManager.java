package cn.mwee.base_common.helper.lock.distributed.zk;

import lombok.Getter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.DisposableBean;

/**
 * Created by liaomengge on 17/12/19.
 */
public class CuratorFrameworkManager implements DisposableBean {

    private String zkServers;
    private int sessionTimeoutMs;//默认为60000ms
    private int connectionTimeoutMs;//默认为15000ms

    @Getter
    private CuratorFramework curatorFramework;

    public CuratorFrameworkManager(String zkServers, int sessionTimeoutMs, int connectionTimeoutMs) {
        this.zkServers = zkServers;
        this.sessionTimeoutMs = sessionTimeoutMs;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.curatorFramework = this.getCuratorClient();
    }

    private CuratorFramework getCuratorClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zkServers, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
        curatorFramework.start();

        return curatorFramework;
    }

    @Override
    public void destroy() {
        CloseableUtils.closeQuietly(this.curatorFramework);
    }
}
