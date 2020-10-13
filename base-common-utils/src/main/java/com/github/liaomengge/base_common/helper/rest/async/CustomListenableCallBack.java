package com.github.liaomengge.base_common.helper.rest.async;

import com.github.liaomengge.base_common.helper.rest.data.BaseRequest;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.github.liaomengge.base_common.utils.string.LyStringUtil;

import java.io.IOException;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.HttpStatusCodeException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Created by liaomengge on 17/3/9.
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class CustomListenableCallBack<T> implements ListenableFutureCallback<T> {

    protected static final Logger log = LyLogger.getInstance(CustomListenableCallBack.class);

    private BaseRequest baseRequest;

    @Override
    public void onFailure(Throwable throwable) {
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        if (throwable instanceof IOException) {
            statusCode = HttpStatus.SERVICE_UNAVAILABLE.value();
        } else if (throwable instanceof HttpStatusCodeException) {
            HttpStatusCodeException httpStatusCodeException = (HttpStatusCodeException) throwable;
            statusCode = httpStatusCodeException.getStatusCode().value();
        }

        String requestParams = "---";
        if (baseRequest != null && baseRequest.getData() != null) {
            requestParams = LyStringUtil.getValue(baseRequest.getData());
        }
        log.error("请求参数[{}], 异步调用服务失败, 状态码[{}], 异常原因 ===> [{}]", requestParams, statusCode, throwable.getMessage());
    }
}
