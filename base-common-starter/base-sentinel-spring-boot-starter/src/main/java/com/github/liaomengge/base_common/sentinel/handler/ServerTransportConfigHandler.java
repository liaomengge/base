package com.github.liaomengge.base_common.sentinel.handler;

import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.github.liaomengge.base_common.sentinel.consts.SentinelClusterConst;
import com.github.liaomengge.base_common.sentinel.entity.ClusterGroupEntity;
import com.github.liaomengge.base_common.sentinel.util.ApolloDataSourceUtil;
import com.github.liaomengge.base_common.sentinel.util.ConverterUtil;
import com.github.liaomengge.base_common.sentinel.util.MachineUtil;
import com.github.liaomengge.base_common.support.stream._Streams;

import java.util.List;
import java.util.Optional;

/**
 * Created by liaomengge on 2021/1/12.
 */
public class ServerTransportConfigHandler extends AbstractSentinelHandler {

    @Override
    public void doInit() throws Exception {
        ReadableDataSource<String, ServerTransportConfig> serverTransportConfigDs =
                ApolloDataSourceUtil.getInstance(SentinelClusterConst.CLUSTER_MAP_DATA_ID, "[]",
                        source -> {
                            List<ClusterGroupEntity> clusterGroupEntities = ConverterUtil.convert(source);
                            return extractServerTransportConfig(clusterGroupEntities).orElse(null);
                        });
        ClusterServerConfigManager.registerServerTransportProperty(serverTransportConfigDs.getProperty());
        log.info(SentinelClusterConst.CLUSTER_MAP_DATA_ID + " server transport config => {}",
                serverTransportConfigDs.readSource());
    }

    private Optional<ServerTransportConfig> extractServerTransportConfig(List<ClusterGroupEntity> clusterGroupEntities) {
        return _Streams.stream(clusterGroupEntities)
                .filter(MachineUtil::machineEqual)
                .findAny()
                .map(groupEntity -> new ServerTransportConfig().setPort(groupEntity.getPort()).setIdleSeconds(ServerTransportConfig.DEFAULT_IDLE_SECONDS));
    }
}
