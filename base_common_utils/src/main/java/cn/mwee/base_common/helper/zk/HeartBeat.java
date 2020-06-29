package cn.mwee.base_common.helper.zk;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

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
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date lastTime;
}
