package cn.ly.service.base_framework.base;

import cn.ly.base_common.utils.string.LyToStringUtil;
import cn.ly.base_common.utils.trace.LyTraceLogUtil;
import cn.ly.service.base_framework.base.code.IResultCode;
import cn.ly.service.base_framework.base.code.SystemResultCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by liaomengge on 17/9/29.
 */
@Setter
@Getter
public class DataResult<T> implements Serializable {

    private static final long serialVersionUID = -885713251933637636L;

    public DataResult() {
        this(true);
    }

    public DataResult(boolean success) {
        this.success = success;
        requestId = LyTraceLogUtil.get();
    }

    public DataResult(T data) {
        this(true);
        this.data = data;
        sysErrCode = SystemResultCode.SUCCESS.getCode();
        sysErrDesc = SystemResultCode.SUCCESS.getDescription();
    }

    public DataResult(boolean success, T data) {
        this(success);
        this.data = data;
    }

    public DataResult(String sysErrCode, String sysErrDesc) {
        this(false);
        this.sysErrCode = sysErrCode;
        this.sysErrDesc = sysErrDesc;
    }

    public DataResult(String sysErrCode, String sysErrDesc, T data) {
        this(sysErrCode, sysErrDesc);
        this.data = data;
    }

    public DataResult(String sysErrCode, String sysErrDesc, String sysException) {
        this(sysErrCode, sysErrDesc);
        this.sysException = sysException;
    }

    public DataResult(String sysErrCode, String sysErrDesc, String sysException, T data) {
        this(sysErrCode, sysErrDesc, sysException);
        this.data = data;
    }

    public DataResult(String sysErrCode, String sysErrDesc, long elapsedMilliseconds) {
        this(sysErrCode, sysErrDesc);
        this.elapsedMilliseconds = elapsedMilliseconds;
    }

    public DataResult(String sysErrCode, String sysErrDesc, long elapsedMilliseconds, T data) {
        this(sysErrCode, sysErrDesc, elapsedMilliseconds);
        this.data = data;
    }

    public DataResult(IResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getDescription());
    }

    public DataResult(IResultCode resultCode, String sysErrDesc) {
        this(resultCode.getCode(), sysErrDesc);
    }

    public DataResult(IResultCode resultCode, String sysErrDesc, String sysException) {
        this(resultCode, sysErrDesc);
        this.sysException = sysException;
    }

    /****************************************华丽分分割线****************************************/

    public static <T> DataResult<T> success() {
        DataResult<T> dataResult = new DataResult<>(true);
        dataResult.setSysErrCode(SystemResultCode.SUCCESS.getCode());
        dataResult.setSysErrDesc(SystemResultCode.SUCCESS.getDescription());
        return dataResult;
    }

    public static <T> DataResult<T> success(T data) {
        return new DataResult<>(data);
    }

    public static <T> DataResult<T> fail() {
        return fail(SystemResultCode.SERVER_ERROR.getDescription());
    }

    public static <T> DataResult<T> fail(String sysErrDesc) {
        return fail(SystemResultCode.SERVER_ERROR, sysErrDesc);
    }

    public static <T> DataResult<T> fail(IResultCode resultCode) {
        return fail(resultCode, resultCode.getDescription());
    }

    public static <T> DataResult<T> fail(IResultCode resultCode, String sysErrDesc) {
        return fail(resultCode, sysErrDesc, "");
    }

    public static <T> DataResult<T> fail(IResultCode resultCode, String sysErrDesc, String sysException) {
        DataResult<T> dataResult = new DataResult<>(false);
        dataResult.setSysErrCode(resultCode.getCode());
        dataResult.setSysErrDesc(sysErrDesc);
        dataResult.setSysException(sysException);
        return dataResult;
    }

    /****************************************华丽分分割线****************************************/

    public static <T> DataResult<T> of(T data) {
        return new DataResult<>(data);
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
    private String requestId;

    @Override
    public String toString() {
        return LyToStringUtil.toString(this);
    }

}
