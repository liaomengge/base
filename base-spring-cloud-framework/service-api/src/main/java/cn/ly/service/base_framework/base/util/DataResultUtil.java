package cn.ly.service.base_framework.base.util;

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
        return Optional.ofNullable(result).map(val -> val.isSuccess()).orElse(Boolean.FALSE).booleanValue();
    }

    public <T> boolean isFail(DataResult<T> result) {
        return !isSuccess(result);
    }

    public <T> T getData(DataResult<T> result) {
        return getOptionalData(result).orElse(null);
    }

    public <T> Optional<T> getOptionalData(DataResult<T> result) {
        return Optional.ofNullable(result).filter(val -> val.isSuccess()).map(DataResult::getData);
    }

    public <T, R> R getData(DataResult<T> result, Function<T, R> function) {
        return getOptionalData(result, function).orElse(null);
    }

    public <T, R> Optional<R> getOptionalData(DataResult<T> result, Function<T, R> function) {
        return Optional.ofNullable(result).filter(val -> val.isSuccess()).map(DataResult::getData).map(function);
    }
}
