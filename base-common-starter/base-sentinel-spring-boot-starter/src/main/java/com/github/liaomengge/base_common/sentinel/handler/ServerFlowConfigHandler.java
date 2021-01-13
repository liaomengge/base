package com.github.liaomengge.base_common.sentinel.handler;

import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerFlowConfig;
import com.alibaba.csp.sentinel.datasource.Converter;
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
 * Created by liaomengge on 2021/1/13.
 */
public class ServerFlowConfigHandler extends AbstractSentinelHandler {

    @Override
    public void doInit() throws Exception {
        ReadableDataSource<String, ServerFlowConfig> serverFlowConfigDs =
                ApolloDataSourceUtil.getInstance(SentinelClusterConst.CLUSTER_MAP_DATA_ID, "[]", new Converter<String
                        , ServerFlowConfig>() {
                    @Override
                    public ServerFlowConfig convert(String source) {
                        List<ClusterGroupEntity> clusterGroupEntities = ConverterUtil.convert(source);
                        return extractServerFlowConfig(clusterGroupEntities).orElse(null);
                    }
                });
        ClusterServerConfigManager.registerGlobalServerFlowProperty(serverFlowConfigDs.getProperty());
        log.info(SentinelClusterConst.CLUSTER_MAP_DATA_ID + " server flow config => {}",
                serverFlowConfigDs.readSource());
    }

    private Optional<ServerFlowConfig> extractServerFlowConfig(List<ClusterGroupEntity> clusterGroupEntities) {
        return _Streams.stream(clusterGroupEntities)
                .filter(MachineUtil::machineEqual)
                .findAny()
                .map(groupEntity -> new ServerFlowConfig()
                        .setExceedCount(ClusterServerConfigManager.getExceedCount())
                        .setIntervalMs(ClusterServerConfigManager.getIntervalMs())
                        .setMaxAllowedQps(groupEntity.getMaxAllowedQps())
                        .setMaxOccupyRatio(ClusterServerConfigManager.getMaxOccupyRatio())
                        .setSampleCount(ClusterServerConfigManager.getSampleCount()));
    }
}
