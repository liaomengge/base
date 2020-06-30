package cn.ly.base_common.helper.rest.sync.retry;

import cn.ly.base_common.utils.log4j2.MwLogger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * Created by liaomengge on 2019/4/29.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpRetryHandler implements HttpRequestRetryHandler {

    private static final Logger logger = MwLogger.getInstance(HttpRetryHandler.class);

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
            logger.info("retry times: " + executionCount);
            return true;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
        HttpRequest request = clientContext.getRequest();
        // 如果请求是幂等的, 就再次尝试
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            logger.info("retry times: " + executionCount);
            return true;
        }
        return false;
    }
}
