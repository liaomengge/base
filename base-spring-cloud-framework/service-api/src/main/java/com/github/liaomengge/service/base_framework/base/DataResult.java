package com.github.liaomengge.service.base_framework.base;

import com.github.liaomengge.base_common.utils.string.LyToStringUtil;
import com.github.liaomengge.service.base_framework.base.code.IResultCode;
import com.github.liaomengge.service.base_framework.base.code.SystemResultCode;
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
    }

    public DataResult(T data) {
        this(true);
        this.data = data;
        sysCode = SystemResultCode.SUCCESS.getCode();
        sysMsg = SystemResultCode.SUCCESS.getMsg();
    }

    public DataResult(boolean success, T data) {
        this(success);
        this.data = data;
    }

    public DataResult(String sysCode, String sysMsg) {
        this(false);
        this.sysCode = sysCode;
        this.sysMsg = sysMsg;
    }

    public DataResult(String sysCode, String sysMsg, T data) {
        this(sysCode, sysMsg);
        this.data = data;
    }

    public DataResult(String sysCode, String sysMsg, String sysException) {
        this(sysCode, sysMsg);
        this.sysException = sysException;
    }

    public DataResult(String sysCode, String sysMsg, String sysException, T data) {
        this(sysCode, sysMsg, sysException);
        this.data = data;
    }

    public DataResult(String sysCode, String sysMsg, long elapsedMilliSeconds) {
        this(sysCode, sysMsg);
        this.elapsedMilliSeconds = elapsedMilliSeconds;
    }

    public DataResult(String sysCode, String sysMsg, long elapsedMilliSeconds, T data) {
        this(sysCode, sysMsg, elapsedMilliSeconds);
        this.data = data;
    }

    public DataResult(IResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMsg());
    }

    public DataResult(IResultCode resultCode, String sysMsg) {
        this(resultCode.getCode(), sysMsg);
    }

    public DataResult(IResultCode resultCode, String sysMsg, String sysException) {
        this(resultCode, sysMsg);
        this.sysException = sysException;
    }

    /************************************************华丽的分割线*******************************************************/

    public static <T> DataResult<T> success() {
        DataResult<T> dataResult = new DataResult<>(true);
        dataResult.setSysCode(SystemResultCode.SUCCESS.getCode());
        dataResult.setSysMsg(SystemResultCode.SUCCESS.getMsg());
        return dataResult;
    }

    public static <T> DataResult<T> success(T data) {
        return new DataResult<>(data);
    }

    public static <T> DataResult<T> fail() {
        return fail(SystemResultCode.SERVER_ERROR.getMsg());
    }

    public static <T> DataResult<T> fail(String sysMsg) {
        return fail(SystemResultCode.SERVER_ERROR, sysMsg);
    }

    public static <T> DataResult<T> fail(IResultCode resultCode) {
        return fail(resultCode, resultCode.getMsg());
    }

    public static <T> DataResult<T> fail(IResultCode resultCode, String sysMsg) {
        return fail(resultCode, sysMsg, "");
    }

    public static <T> DataResult<T> fail(IResultCode resultCode, String sysMsg, String sysException) {
        DataResult<T> dataResult = new DataResult<>(false);
        dataResult.setSysCode(resultCode.getCode());
        dataResult.setSysMsg(sysMsg);
        dataResult.setSysException(sysException);
        return dataResult;
    }

    /************************************************华丽的分割线*******************************************************/

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
    private String sysCode = "";

    /**
     * 错误描述
     */
    private String sysMsg = "";

    /**
     * 异常详情
     */
    private String sysException = "";

    /**
     * 处理耗时(秒)
     */
    private long elapsedMilliSeconds;

    @Override
    public String toString() {
        return LyToStringUtil.toString(this);
    }

}
