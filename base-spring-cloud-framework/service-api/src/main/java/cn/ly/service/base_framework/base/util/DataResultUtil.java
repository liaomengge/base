package cn.ly.service.base_framework.base.util;

import cn.ly.service.base_framework.base.BaseResponse;
import cn.ly.service.base_framework.base.DataResult;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by liaomengge on 2020/5/21.
 */
@UtilityClass
public class DataResultUtil {

    public <T> boolean isSuccess(DataResult<T> result) {
        return Optional.ofNullable(result).map(DataResult::isSuccess).orElse(Boolean.FALSE).booleanValue();
    }

    public <T> boolean isFail(DataResult<T> result) {
        return !isSuccess(result);
    }

    public <T> T getData(DataResult<T> result) {
        return getOptionalData(result).orElse(null);
    }

    public <T> Optional<T> getOptionalData(DataResult<T> result) {
        return Optional.ofNullable(result).filter(DataResult::isSuccess).map(DataResult::getData);
    }

    public <T, R> R getData(DataResult<T> result, Function<T, R> function) {
        return getOptionalData(result, function).orElse(null);
    }

    public <T, R> Optional<R> getOptionalData(DataResult<T> result, Function<T, R> function) {
        return Optional.ofNullable(result).filter(DataResult::isSuccess).map(DataResult::getData).map(function);
    }

    /************************************************华丽的分割线*******************************************************/

    public <T, R extends BaseResponse<T>> boolean isResponseSuccess(DataResult<R> result) {
        return Optional.ofNullable(result).filter(DataResult::isSuccess).map(DataResult::getData)
                .map(BaseResponse::isSuccess).orElse(Boolean.FALSE)
                .booleanValue();
    }

    public <T, R extends BaseResponse<T>> boolean isResponseFail(DataResult<R> result) {
        return !isResponseSuccess(result);
    }

    public <T, R extends BaseResponse<T>> T getResponseData(DataResult<R> result) {
        return getOptionalResponseData(result).orElse(null);
    }

    public <T, R extends BaseResponse<T>> Optional<T> getOptionalResponseData(DataResult<R> result) {
        return Optional.ofNullable(result).filter(DataResult::isSuccess).map(DataResult::getData)
                .filter(BaseResponse::isSuccess)
                .map(BaseResponse::getData);
    }

    public <T, R extends BaseResponse<T>, RS> RS getResponseData(DataResult<R> result, Function<T, RS> function) {
        return getOptionalResponseData(result, function).orElse(null);
    }

    public <T, R extends BaseResponse<T>, RS> Optional<RS> getOptionalResponseData(DataResult<R> result,
                                                                                   Function<T, RS> function) {
        return Optional.ofNullable(result).filter(DataResult::isSuccess).map(DataResult::getData)
                .filter(BaseResponse::isSuccess)
                .map(BaseResponse::getData)
                .map(function);
    }
}
