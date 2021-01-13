package com.github.liaomengge.base_common.sentinel.util;

import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.alibaba.csp.sentinel.util.HostNameUtil;
import com.github.liaomengge.base_common.sentinel.entity.ClusterGroupEntity;
import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2021/1/12.
 */
@UtilityClass
public class MachineUtil {

    public boolean machineEqual(ClusterGroupEntity clusterGroupEntity) {
        return getCurrentMachineId().equals(clusterGroupEntity.getMachineId());
    }

    public String getCurrentMachineId() {
        return HostNameUtil.getIp() + '@' + TransportConfig.getRuntimePort();
    }
}
