package com.github.liaomengge.base_common.sentinel.handler;

import com.alibaba.csp.sentinel.cluster.ClusterConstants;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.github.liaomengge.base_common.sentinel.consts.SentinelClusterConst;
import com.github.liaomengge.base_common.sentinel.util.ApolloDataSourceUtil;

/**
 * Created by liaomengge on 2021/1/12.
 */
public class ClientConfigHandler extends AbstractSentinelHandler {

    @Override
    public void doInit() throws Exception {
        ReadableDataSource<String, ClusterClientConfig> clientConfigDs =
                ApolloDataSourceUtil.getInstance(SentinelClusterConst.CLIENT_CONFIG_DATA_ID,
                        String.valueOf(ClusterConstants.DEFAULT_REQUEST_TIMEOUT));
        ClusterClientConfigManager.registerClientConfigProperty(clientConfigDs.getProperty());
        log.info(SentinelClusterConst.CLIENT_CONFIG_DATA_ID + " => {}", clientConfigDs.readSource());
    }
}
