package cn.mwee.service.base_framework.common.config;

import lombok.Data;

/**
 * Created by liaomengge on 2018/9/21.
 */
@Data
public class FilterConfig {

    /**
     * 服务方的签名
     */
    private String signConfig = "";//签名配置

    /**
     * 快速失败的方法名
     */
    private String failFastMethodName = "";//以逗号分隔

    /**
     * 限流的方法名&qps
     */
    private String rateLimitConfig = "";//限流配置

    /**
     * 忽略日志的方法
     */
    private String ignoreLogMethodName = "";//以逗号分隔
}
