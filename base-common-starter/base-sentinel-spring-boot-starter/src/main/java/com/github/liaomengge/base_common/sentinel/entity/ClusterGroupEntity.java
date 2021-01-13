package com.github.liaomengge.base_common.sentinel.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * @author Eric Zhao
 * @since 1.4.1
 */
@Data
@Builder
public class ClusterGroupEntity {

    private String machineId;
    private String ip;
    private Integer port;
    private Set<String> clientSet;
    private Double maxAllowedQps;
}
