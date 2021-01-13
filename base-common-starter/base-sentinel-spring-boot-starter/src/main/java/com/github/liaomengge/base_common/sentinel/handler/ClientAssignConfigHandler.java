package com.github.liaomengge.base_common.sentinel.handler;

import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientAssignConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.github.liaomengge.base_common.sentinel.consts.SentinelClusterConst;
import com.github.liaomengge.base_common.sentinel.entity.ClusterGroupEntity;
import com.github.liaomengge.base_common.sentinel.util.ApolloDataSourceUtil;
import com.github.liaomengge.base_common.sentinel.util.ConverterUtil;
import com.github.liaomengge.base_common.sentinel.util.MachineUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by liaomengge on 2021/1/12.
 */
public class ClientAssignConfigHandler extends AbstractSentinelHandler {

    @Override
    public void doInit() throws Exception {
        // Cluster map format:
        // [{"clientSet":["112.12.88.66@8729","112.12.88.67@8727"],"ip":"112.12.88.68","machineId":"112.12.88
        // .68@8728","port":11111}]
        // machineId: <ip@commandPort>, commandPort for port exposed to Sentinel dashboard (transport module)
        ReadableDataSource<String, ClusterClientAssignConfig> clientAssignConfigDs =
                ApolloDataSourceUtil.getInstance(SentinelClusterConst.CLUSTER_MAP_DATA_ID, "[]",
                        source -> {
                            List<ClusterGroupEntity> clusterGroupEntities = ConverterUtil.convert(source);
                            return extractClientAssignment(clusterGroupEntities).orElse(null);
                        });
        ClusterClientConfigManager.registerServerAssignProperty(clientAssignConfigDs.getProperty());
        log.info(SentinelClusterConst.CLUSTER_MAP_DATA_ID + " client assign config => {}",
                clientAssignConfigDs.readSource());
    }

    private Optional<ClusterClientAssignConfig> extractClientAssignment(List<ClusterGroupEntity> groupList) {
        if (CollectionUtils.isEmpty(groupList)) {
            return Optional.empty();
        }
        if (groupList.stream().anyMatch(MachineUtil::machineEqual)) {
            return Optional.empty();
        }
        // Build client assign config from the client set of target server group.
        for (ClusterGroupEntity group : groupList) {
            if (group.getClientSet().contains(MachineUtil.getCurrentMachineId())) {
                String ip = group.getIp();
                Integer port = group.getPort();
                return Optional.of(new ClusterClientAssignConfig(ip, port));
            }
        }
        return Optional.empty();
    }
}
