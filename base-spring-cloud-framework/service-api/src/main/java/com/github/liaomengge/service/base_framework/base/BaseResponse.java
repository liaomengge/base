package com.github.liaomengge.service.base_framework.base;

import com.github.liaomengge.base_common.utils.string.LyToStringUtil;
import com.github.liaomengge.service.base_framework.base.code.IResultCode;
import com.github.liaomengge.service.base_framework.base.code.SystemResultCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Optional;

/**
 * Created by liaomengge on 16/4/12.
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -7757311391986476066L;

    /**
     * 业务返回码 000000表示成功
     */
    private String code;

    /**
     * 业务返回约定的文案
     */
    private String msg;

    private T data;

    public BaseResponse() {
        code = SystemResultCode.SUCCESS.getCode();
        msg = SystemResultCode.SUCCESS.getMsg();
    }

    public BaseResponse(T data) {
        this();
        this.data = data;
    }

    public BaseResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResponse(String code, String msg, T data) {
        this(code, msg);
        this.data = data;
    }

    public BaseResponse(IResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMsg());
    }

    public BaseResponse(IResultCode resultCode, String msg) {
        this(resultCode.getCode(), msg);
    }

    /************************************************华丽的分割线*******************************************************/

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>();
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(data);
    }

    public static <T> BaseResponse<T> fail(IResultCode resultCode) {
        return new BaseResponse<>(resultCode);
    }

    public static <T> BaseResponse<T> fail(IResultCode resultCode, String msg) {
        return new BaseResponse<>(resultCode, msg);
    }

    public static <T> boolean isSuccess(BaseResponse<T> response) {
        return Optional.ofNullable(response).map(BaseResponse::getCode).map(val ->
                StringUtils.equalsIgnoreCase(val, SystemResultCode.SUCCESS.getCode())).orElse(Boolean.FALSE).booleanValue();
    }

    public static <T> boolean isFail(BaseResponse<T> response) {
        return !isSuccess(response);
    }

    public static <T> T getDate(BaseResponse<T> response) {
        return Optional.ofNullable(response).filter(val -> StringUtils.equalsIgnoreCase(val.getCode(),
                SystemResultCode.SUCCESS.getCode())).map(BaseResponse::getData).orElse(null);
    }

    public static <T> BaseResponse<T> of(T data) {
        return new BaseResponse<>(data);
    }

    @Override
    public String toString() {
        return LyToStringUtil.toString(this);
    }
}
