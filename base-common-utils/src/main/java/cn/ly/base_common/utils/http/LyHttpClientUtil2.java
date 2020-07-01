package cn.ly.base_common.utils.http;

import cn.ly.base_common.utils.log.LyAlarmLogUtil;
import cn.ly.base_common.helper.rest.sync.retry.HttpRetryHandler;
import cn.ly.base_common.support.exception.CommunicationException;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.thread.LyThreadFactoryBuilderUtil;
import lombok.experimental.UtilityClass;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 16/12/13.
 */
@UtilityClass
public class LyHttpClientUtil2 {

    private final Logger logger = LyLogger.getInstance(LyHttpClientUtil2.class);

    private int RETRY_TIMES = 3;//重试3次
    private int DEFAULT_TIME_OUT = 5_000;//默认5秒超时

    private int MAX_TOTAL = 512;//最大路由数
    private int DEFAULT_MAX_PER_ROUTE = 256;//每个路由最大数

    private PoolingHttpClientConnectionManager poolConnManager;
    private CloseableHttpClient httpClient;

    {
        poolConnManager = new PoolingHttpClientConnectionManager();
        poolConnManager.setMaxTotal(MAX_TOTAL);
        poolConnManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        httpClient =
                HttpClients.custom().setConnectionManager(poolConnManager).setRetryHandler(new HttpRetryHandler(RETRY_TIMES)).build();
        closeIdleExpiredConnections(poolConnManager);
    }

    /*****************************************************************
     * GET
     ***************************************************************/

    public String get(String url) {
        return get(url, "utf-8", null);
    }

    public String get(String url, Header[] headers) {
        return get(url, "utf-8", headers);
    }

    public String get(String url, int timeoutMilliSeconds) {
        return get(url, timeoutMilliSeconds, null);
    }

    public String get(String url, int timeoutMilliSeconds, Header[] headers) {
        return get(url, "utf-8", timeoutMilliSeconds, headers);
    }

    public String get(String url, String encoding, Header[] headers) {
        return get(url, encoding, DEFAULT_TIME_OUT, headers);
    }

    public String get(String url, String encoding, int timeoutMilliSeconds) {
        return get(url, encoding, timeoutMilliSeconds, null);
    }

    public String get(String url, String encoding, int timeoutMilliSeconds, Header[] headers) {
        String result = null;
        CloseableHttpResponse httpResponse = null;
        try {
            RequestConfig config =
                    RequestConfig.custom().setConnectTimeout(timeoutMilliSeconds).setSocketTimeout(timeoutMilliSeconds).setConnectionRequestTimeout(timeoutMilliSeconds).build();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(config);
            if (headers != null && headers.length > 0) {
                httpGet.setHeaders(headers);
            }
            httpResponse = httpClient.execute(httpGet);
            result = EntityUtils.toString(httpResponse.getEntity(), encoding);
        } catch (Throwable t) {
            handleThrowable(t, url);
        } finally {
            closeQuietly(httpResponse);
        }
        return result;
    }

    /*****************************************************************
     * POST JSON
     ***************************************************************/

    public String post(String url) {
        return post(url, "", "application/json", RETRY_TIMES);
    }

    public String post(String url, int reTryTimes) {
        return post(url, "", "application/json", reTryTimes);
    }

    public String post(String url, String postData, int timeoutMilliSeconds) {
        return post(url, postData, timeoutMilliSeconds, null);
    }

    public String post(String url, String postData, int timeoutMilliSeconds, Header[] header) {
        return post(url, postData, "application/json", "utf-8", timeoutMilliSeconds, header);
    }

    public String post(String url, String postData, String mediaType, int reTryTimes) {
        return post(url, postData, mediaType, "utf-8", null);
    }

    public String post(String url, String postData, String mediaType, Header[] header, int reTryTimes) {
        return post(url, postData, mediaType, "utf-8", header);
    }

    public String post(String url, String postData, String mediaType, String encoding, int reTryTimes) {
        return post(url, postData, mediaType, encoding, null);
    }

    public String post(String url, String postData, String mediaType, String encoding, Header[] headers) {
        return post(url, postData, mediaType, encoding, DEFAULT_TIME_OUT, headers);
    }

    public String post(String url, String postData, String mediaType, String encoding, int timeoutMilliSeconds
            , Header[] headers) {
        String result = null;
        CloseableHttpResponse httpResponse = null;
        try {
            RequestConfig config =
                    RequestConfig.custom().setConnectTimeout(timeoutMilliSeconds).setSocketTimeout(timeoutMilliSeconds).setConnectionRequestTimeout(timeoutMilliSeconds).build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(config);

            StringEntity entity = new StringEntity(postData, encoding);
            entity.setContentType(mediaType);
            httpPost.setEntity(entity);
            if (headers != null && headers.length > 0) {
                httpPost.setHeaders(headers);
            }
            httpResponse = httpClient.execute(httpPost);
            result = EntityUtils.toString(httpResponse.getEntity(), encoding);
        } catch (Throwable t) {
            handleThrowable(t, url);
        } finally {
            closeQuietly(httpResponse);
        }
        return result;
    }

    /*****************************************************************
     * POST FORM
     ***************************************************************/

    public String doFormPost(String url, List<NameValuePair> params, int timeoutMilliSeconds) {
        return doFormPost(url, params, timeoutMilliSeconds, null);
    }

    public String doFormPost(String url, List<NameValuePair> params, int timeoutMilliSeconds, Header[] headers) {
        return doFormPost(url, params, "application/x-www-form-urlencoded", "utf-8", timeoutMilliSeconds, headers
        );
    }

    public String doFormPost(String url, List<NameValuePair> params, String mediaType) {
        return doFormPost(url, params, mediaType, "utf-8");
    }

    public String doFormPost(String url, List<NameValuePair> params, String mediaType, Header[] headers) {
        return doFormPost(url, params, mediaType, "utf-8", headers);
    }

    public String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding) {
        return doFormPost(url, params, mediaType, encoding, DEFAULT_TIME_OUT);
    }

    public String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
                             Header[] headers) {
        return doFormPost(url, params, mediaType, encoding, DEFAULT_TIME_OUT, headers);
    }

    public String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
                             int timeoutMilliSeconds) {
        return doFormPost(url, params, mediaType, encoding, timeoutMilliSeconds, null);
    }

    public String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
                             int timeoutMilliSeconds, Header[] headers) {
        String result = null;
        CloseableHttpResponse httpResponse = null;
        try {
            RequestConfig config =
                    RequestConfig.custom().setConnectTimeout(timeoutMilliSeconds).setSocketTimeout(timeoutMilliSeconds).setConnectionRequestTimeout(timeoutMilliSeconds).build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(config);

            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, encoding);
            formEntity.setContentType(mediaType);
            httpPost.setEntity(formEntity);
            if (headers != null && headers.length > 0) {
                httpPost.setHeaders(headers);
            }

            httpResponse = httpClient.execute(httpPost);

            result = EntityUtils.toString(httpResponse.getEntity(), encoding);
        } catch (Throwable t) {
            handleThrowable(t, url);
        } finally {
            closeQuietly(httpResponse);
        }
        return result;
    }

    private static void handleThrowable(Throwable t, String url) {
        if (t instanceof InterruptedIOException || (Objects.nonNull(t.getCause()) && t.getCause() instanceof InterruptedIOException)) {
            LyAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_HTTP.error(t);
        } else {
            LyAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_BIZ.error(t);
        }
        logger.warn("调用服务失败, 服务地址: " + url, t);
        throw new CommunicationException("调用服务失败, 服务地址: " + url + ", 异常类型: " + t.getClass() + ", 错误原因: " + t.getMessage());
    }

    private void closeQuietly(CloseableHttpResponse httpResponse) {
        if (Objects.nonNull(httpResponse)) {
            try {
                httpResponse.close();
            } catch (IOException e) {
                logger.error("关闭http response失败", e);
            }
        }
    }

    private void closeIdleExpiredConnections(PoolingHttpClientConnectionManager connectionManager) {
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1,
                LyThreadFactoryBuilderUtil.build("http-client-idle"));
        service.scheduleAtFixedRate(() -> {
            try {
                connectionManager.closeExpiredConnections();
                connectionManager.closeIdleConnections(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("close expired/idle connections exception", e);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
}
