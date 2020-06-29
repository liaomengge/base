package cn.mwee.service.base_framework.common.consts;

/**
 * Created by liaomengge on 2016/8/30.
 */
public class MetricsConst {

    /**
     * 前缀(B端）
     */
    public static final String PREFIX_SERVICE_NAME = "base_spring_cloud_framework";

    /**
     * 请求执行总数
     */
    public static final String REQ_ALL = "req_all";

    /**
     * 请求执行正常次数
     */
    public static final String REQ_EXE_SUC = ".req.suc";

    /**
     * 请求执行异常次数
     */
    public static final String REQ_EXE_FAIL = ".req.fail";

    /**
     * 请求执行繁忙
     */
    public static final String REQ_EXE_BUSY = ".req.busy";

    /**
     * 请求执行时间
     */
    public static final String REQ_EXE_TIME = ".req.time";

    /**
     * 签名失败的次数
     */
    public static final String SIGN_EXE_FAIL = ".sign.fail";

    /**
     * 快速失败的次数
     */
    public static final String FAIL_FAST_EXE_FAIL = ".fail_fast.fail";
}