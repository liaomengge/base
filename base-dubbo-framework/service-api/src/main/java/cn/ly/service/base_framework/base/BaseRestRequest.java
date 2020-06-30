package cn.ly.service.base_framework.base;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by liaomengge on 17/9/29.
 */
@Data
public abstract class BaseRestRequest implements Serializable {
    private static final long serialVersionUID = -8516599114094145340L;

    private String appId;
    private String language;
    private String sign;
    private String timeZone;
    private long timestamp;
}
