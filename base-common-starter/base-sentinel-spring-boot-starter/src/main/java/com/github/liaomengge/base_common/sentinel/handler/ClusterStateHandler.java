package com.github.liaomengge.base_common.sentinel.handler;

import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.github.liaomengge.base_common.sentinel.consts.SentinelClusterConst;
import com.github.liaomengge.base_common.sentinel.entity.ClusterGroupEntity;
import com.github.liaomengge.base_common.sentinel.util.ApolloDataSourceUtil;
import com.github.liaomengge.base_common.sentinel.util.ConverterUtil;
import com.github.liaomengge.base_common.sentinel.util.MachineUtil;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;

/**
 * Created by liaomengge on 2021/1/12.
 */
public class ClusterStateHandler extends AbstractSentinelHandler {

    @Override
    public void doInit() throws Exception {
        ReadableDataSource<String, Integer> clusterModeDs =
                ApolloDataSourceUtil.getInstance(SentinelClusterConst.CLUSTER_MAP_DATA_ID, "[]",
                        source -> {
                            List<ClusterGroupEntity> clusterGroupEntities =
                                    ConverterUtil.convert(Lists.newArrayList(), source);
                            return extractMode(clusterGroupEntities);
                        });
        ClusterStateManager.registerProperty(clusterModeDs.getProperty());
        log.info(SentinelClusterConst.CLUSTER_MAP_DATA_ID + " cluster mode => {}", clusterModeDs.readSource());
    }

    private int extractMode(List<ClusterGroupEntity> groupList) {
        // If any server group machineId matches current, then it's token server.
        if (groupList.stream().anyMatch(MachineUtil::machineEqual)) {
            return ClusterStateManager.CLUSTER_SERVER;
        }
        // If current machine belongs to any of the token server group, then it's token client.
        // Otherwise it's unassigned, should be set to NOT_STARTED.
        boolean canBeClient = groupList.stream()
                .flatMap(e -> e.getClientSet().stream())
                .filter(Objects::nonNull)
                .anyMatch(e -> e.equals(MachineUtil.getCurrentMachineId()));
        return canBeClient ? ClusterStateManager.CLUSTER_CLIENT : ClusterStateManager.CLUSTER_NOT_STARTED;
    }
}
