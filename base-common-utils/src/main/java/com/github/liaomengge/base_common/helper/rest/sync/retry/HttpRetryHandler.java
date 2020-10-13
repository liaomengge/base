package com.github.liaomengge.base_common.helper.rest.sync.retry;

import com.github.liaomengge.base_common.utils.log4j2.LyLogger;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by liaomengge on 2019/4/29.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpRetryHandler implements HttpRequestRetryHandler {

    private static final Logger log = LyLogger.getInstance(HttpRetryHandler.class);

    private int reTryTimes = 3;//默认重试3次

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext httpContext) {
        if (executionCount >= reTryTimes) {
            // Do not retry if over max retry count
            return false;
        }
        if (exception instanceof UnknownHostException) {
            // Unknown host
            return false;
        }
        if (exception instanceof SSLException) {
            // SSL handshake exception
            return false;
        }
        if (exception instanceof InterruptedIOException || exception instanceof NoHttpResponseException) {
            // Timeout OR 服务端断开连接
            log.info("retry times: " + executionCount);
            return true;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
        HttpRequest request = clientContext.getRequest();
        // 如果请求是幂等的, 就再次尝试
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            log.info("retry times: " + executionCount);
            return true;
        }
        return false;
    }
}
