package cn.ly.service.base_framework.common.consts;

/**
 * Created by liaomengge on 2016/8/30.
 */
public interface MetricsConst {

    /**
     * 前缀
     */
    String PREFIX_SERVICE_NAME = "base-spring-cloud-framework";

    /**
     * 请求执行总数
     */
    String REQ_ALL = "req_all";

    /**
     * 请求执行正常次数
     */
    String REQ_EXE_SUC = ".req.suc";

    /**
     * 请求执行异常次数
     */
    String REQ_EXE_FAIL = ".req.fail";

    /**
     * 请求执行繁忙
     */
    String REQ_EXE_BUSY = ".req.busy";

    /**
     * 请求执行时间
     */
    String REQ_EXE_TIME = ".req.time";

    /**
     * 签名失败的次数
     */
    String SIGN_EXE_FAIL = ".sign.fail";

    /**
     * 快速失败的次数
     */
    String FAIL_FAST_EXE_FAIL = ".fail_fast.fail";
}