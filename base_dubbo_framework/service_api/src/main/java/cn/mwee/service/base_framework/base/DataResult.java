package cn.mwee.service.base_framework.base;

import cn.mwee.base_common.utils.string.MwToStringUtil;
import cn.mwee.base_common.utils.trace.MwTraceLogUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DataResult<T> implements Serializable {

    private static final long serialVersionUID = -7793112364650981322L;

    public DataResult() {
        this(true);
    }

    public DataResult(boolean success) {
        this.success = success;
        this.traceId = MwTraceLogUtil.get();
    }

    public DataResult(T data) {
        this(true);
        this.data = data;
    }

    public DataResult(String sysErrCode, String sysErrDesc) {
        this(false);
        this.sysErrCode = sysErrCode;
        this.sysErrDesc = sysErrDesc;
    }

    public DataResult(String sysErrCode, String sysErrDesc, String sysException) {
        this(sysErrCode, sysErrDesc);
        this.sysException = sysException;
    }

    public DataResult(String sysErrCode, String sysErrDesc, long elapsedMilliseconds) {
        this(sysErrCode, sysErrDesc);
        this.elapsedMilliseconds = elapsedMilliseconds;
    }

    /**
     * 是否处理成功
     */
    private boolean success;

    /**
     * 返回的数据
     */
    private T data;

    /**
     * 错误代码
     */
    private String sysErrCode = "";

    /**
     * 错误描述
     */
    private String sysErrDesc = "";

    /**
     * 异常详情
     */
    private String sysException = "";

    /**
     * 处理耗时(毫秒)
     */
    private long elapsedMilliseconds;

    /**
     * 调用链id
     */
    private String traceId;

    @Override
    public String toString() {
        return MwToStringUtil.toString(this);
    }

}
