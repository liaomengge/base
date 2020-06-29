package cn.mwee.service.base_framework.common.config;

import cn.mwee.service.base_framework.common.consts.MetricsConst;
import lombok.Data;

/**
 * Created by liaomengge on 2016/8/30.
 * 一些服务的公用配置
 */
@Data
public class ServiceConfig {

    /**
     * 服务名
     */
    private String serviceName = MetricsConst.PREFIX_SERVICE_NAME;

    /**
     * 运行环境(DEV,TEST,PROD)
     */
    private String env;

    /**
     * 运行端口(RPC服务)
     */
    private int rpcPort;

    /**
     * 版本号
     */
    private String version;

    /**
     * 是否校验签名
     */
    private boolean checkSign = false;

    /**
     * 发生错误时是否发送邮件
     */
    private boolean sendEmail = false;

    /**
     * service provider发生错误时,异常显示返回
     */
    private boolean throwException = false;
}
