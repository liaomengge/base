package com.github.liaomengge.base_common.helper.zk;

import lombok.Data;

/**
 * Created by liaomengge on 5/27/16.
 */
@Data
public class HeartBeat {

    /**
     * 主机IP
     */
    private String hostIp;

    /**
     * 主机名
     */
    private String hostName;

    /**
     * 最后心跳时间
     */
    private String lastTime;
}
