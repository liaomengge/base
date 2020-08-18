package cn.ly.service.base_framework.base;

import cn.ly.base_common.utils.string.LyToStringUtil;
import cn.ly.service.base_framework.base.code.IResultCode;
import cn.ly.service.base_framework.base.code.SystemResultCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Optional;

/**
 * Created by liaomengge on 16/4/12.
 */
@Data
public class BaseRestResponse<T> implements Serializable {

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

    public BaseRestResponse() {
        code = SystemResultCode.SUCCESS.getCode();
        msg = SystemResultCode.SUCCESS.getMsg();
    }

    public BaseRestResponse(T data) {
        this();
        this.data = data;
    }

    public BaseRestResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseRestResponse(String code, String msg, T data) {
        this(code, msg);
        this.data = data;
    }

    public BaseRestResponse(IResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMsg());
    }

    public BaseRestResponse(IResultCode resultCode, String msg) {
        this(resultCode.getCode(), msg);
    }

    /****************************************华丽分分割线****************************************/

    public static <T> BaseRestResponse<T> success() {
        return new BaseRestResponse<>();
    }

    public static <T> BaseRestResponse<T> success(T data) {
        return new BaseRestResponse<>(data);
    }

    public static <T> BaseRestResponse<T> fail(IResultCode resultCode) {
        return new BaseRestResponse<>(resultCode);
    }

    public static <T> BaseRestResponse<T> fail(IResultCode resultCode, String msg) {
        return new BaseRestResponse<>(resultCode, msg);
    }

    public static <T> boolean isSuccess(BaseRestResponse<T> response) {
        return Optional.ofNullable(response).map(BaseRestResponse::getCode).map(val ->
                StringUtils.equalsIgnoreCase(val, SystemResultCode.SUCCESS.getCode())).orElse(Boolean.FALSE).booleanValue();
    }

    public static <T> boolean isFail(BaseRestResponse<T> response) {
        return !isSuccess(response);
    }

    public static <T> T getDate(BaseRestResponse<T> response) {
        return Optional.ofNullable(response).filter(val -> StringUtils.equalsIgnoreCase(val.getCode(),
                SystemResultCode.SUCCESS.getCode())).map(BaseRestResponse::getData).orElse(null);
    }

    public static <T> BaseRestResponse<T> of(T data) {
        return new BaseRestResponse<>(data);
    }

    @Override
    public String toString() {
        return LyToStringUtil.toString(this);
    }
}
