package cn.ly.base_common.utils.http;

import cn.ly.base_common.helper.rest.sync.retry.HttpRetryHandler;
import cn.ly.base_common.support.exception.CommunicationException;
import cn.ly.base_common.utils.json.LyJacksonUtil;
import cn.ly.base_common.utils.log.LyAlarmLogUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.properties.LyConfigUtil;
import cn.ly.base_common.utils.properties.LyPropertiesUtil;
import cn.ly.base_common.utils.thread.LyThreadFactoryBuilderUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import lombok.Data;

/**
 * Created by liaomengge on 16/12/13.
 */
public class LyHttpClientUtil3 {

    private static final Logger log = LyLogger.getInstance(LyHttpClientUtil3.class);

    private static final String DEFAULT_HTTPCLIENT_FILE = "classpath:httpclient.properties";

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String DEFAULT_MEDIA_TYPE_JSON = "application/json";
    private static final String DEFAULT_MEDIA_TYPE_FORM = "application/x-www-form-urlencoded";

    private static final int DEFAULT_RETRY_TIMES = 3;//总请求次数,重试2次
    private static final int DEFAULT_TIME_OUT = 5_000;//默认5秒超时

    private static final int DEFAULT_MAX_TOTAL = 512;//最大路由数
    private static final int DEFAULT_MAX_PER_ROUTE = 256;//每个路由最大数

    private static PoolingHttpClientConnectionManager poolConnManager;
    private static CloseableHttpClient httpClient;

    static {
        HttpClientProperties httpClientProperties = initLoad();
        int maxTotal = DEFAULT_MAX_TOTAL, defaultMaxPerRoute = DEFAULT_MAX_PER_ROUTE;
        Map<String, Integer> urlPerRouteMap = null;
        if (Objects.nonNull(httpClientProperties)) {
            maxTotal = httpClientProperties.getMaxTotal();
            defaultMaxPerRoute = httpClientProperties.getDefaultMaxPerRoute();
            String urls = httpClientProperties.getUrls();
            if (StringUtils.isNotBlank(urls)) {
                try {
                    urlPerRouteMap = LyJacksonUtil.fromJson(urls, new TypeReference<Map<String, Integer>>() {
                    });
                } catch (Exception e) {
                    urlPerRouteMap = Maps.newHashMap();
                }

            }
        }
        poolConnManager = new PoolingHttpClientConnectionManager();
        poolConnManager.setMaxTotal(maxTotal);
        poolConnManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        if (MapUtils.isNotEmpty(urlPerRouteMap)) {
            urlPerRouteMap.forEach((key, value) -> {
                if (StringUtils.isNotBlank(key) && Objects.nonNull(value)) {
                    HttpHost httpHost = URIUtils.extractHost(URI.create(key));
                    HttpRoute httpRoute = new HttpRoute(httpHost);
                    poolConnManager.setMaxPerRoute(httpRoute, value);
                }
            });
        }
        httpClient =
                HttpClients.custom().setConnectionManager(poolConnManager).setRetryHandler(new HttpRetryHandler(DEFAULT_RETRY_TIMES)).build();
        closeIdleExpiredConnections(poolConnManager);
    }

    private static HttpClientProperties initLoad() {
        HttpClientProperties httpClientProperties;
        try {
            httpClientProperties = new HttpClientProperties();
            Properties properties = LyConfigUtil.loadProperties(DEFAULT_HTTPCLIENT_FILE);
            if (Objects.nonNull(properties)) {
                httpClientProperties.setMaxTotal(LyPropertiesUtil.getIntProperty(properties, "maxTotal",
                        DEFAULT_MAX_TOTAL));
                httpClientProperties.setDefaultMaxPerRoute(LyPropertiesUtil.getIntProperty(properties,
                        "defaultMaxPerRoute", DEFAULT_MAX_PER_ROUTE));
                httpClientProperties.setUrls(LyPropertiesUtil.getStringProperty(properties, "urls"));
                return httpClientProperties;
            }
        } catch (Exception e) {
            log.warn("load [classpath:httpclient.properties] fail", e);
        }
        return null;
    }

    @Data
    private static class HttpClientProperties {
        private int maxTotal;
        private int defaultMaxPerRoute;
        private String urls;//格式如下：{"url":"maxPerRoute"}
    }

    /*****************************************************************
     * GET
     ***************************************************************/

    public static String get(String url) {
        return get(url, DEFAULT_ENCODING, null);
    }

    public static String get(String url, Header[] headers) {
        return get(url, DEFAULT_ENCODING, headers);
    }

    public static String get(String url, int timeoutMilliSeconds) {
        return get(url, timeoutMilliSeconds, null);
    }

    public static String get(String url, int timeoutMilliSeconds, Header[] headers) {
        return get(url, DEFAULT_ENCODING, timeoutMilliSeconds, headers);
    }

    public static String get(String url, String encoding, Header[] headers) {
        return get(url, encoding, DEFAULT_TIME_OUT, headers);
    }

    public static String get(String url, String encoding, int timeoutMilliSeconds) {
        return get(url, encoding, timeoutMilliSeconds, null);
    }

    public static String get(String url, String encoding, int timeoutMilliSeconds, Header[] headers) {
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

    public static String post(String url) {
        return post(url, "", DEFAULT_MEDIA_TYPE_JSON, DEFAULT_RETRY_TIMES);
    }

    public static String post(String url, int reTryTimes) {
        return post(url, "", DEFAULT_MEDIA_TYPE_JSON, reTryTimes);
    }

    public static String post(String url, String postData, int timeoutMilliSeconds) {
        return post(url, postData, timeoutMilliSeconds, null);
    }

    public static String post(String url, String postData, int timeoutMilliSeconds, Header[] header) {
        return post(url, postData, DEFAULT_MEDIA_TYPE_JSON, DEFAULT_ENCODING, timeoutMilliSeconds, header);
    }

    public static String post(String url, String postData, String mediaType, int reTryTimes) {
        return post(url, postData, mediaType, DEFAULT_ENCODING, null);
    }

    public static String post(String url, String postData, String mediaType, Header[] header, int reTryTimes) {
        return post(url, postData, mediaType, DEFAULT_ENCODING, header);
    }

    public static String post(String url, String postData, String mediaType, String encoding, int reTryTimes) {
        return post(url, postData, mediaType, encoding, null);
    }

    public static String post(String url, String postData, String mediaType, String encoding, Header[] headers) {
        return post(url, postData, mediaType, encoding, DEFAULT_TIME_OUT, headers);
    }

    public static String post(String url, String postData, String mediaType, String encoding, int timeoutMilliSeconds
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

    public static String doFormPost(String url, List<NameValuePair> params, int timeoutMilliSeconds) {
        return doFormPost(url, params, timeoutMilliSeconds, null);
    }

    public static String doFormPost(String url, List<NameValuePair> params, int timeoutMilliSeconds, Header[] headers) {
        return doFormPost(url, params, DEFAULT_MEDIA_TYPE_FORM, DEFAULT_ENCODING, timeoutMilliSeconds,
                headers
        );
    }

    public static String doFormPost(String url, List<NameValuePair> params, String mediaType) {
        return doFormPost(url, params, mediaType, DEFAULT_ENCODING);
    }

    public static String doFormPost(String url, List<NameValuePair> params, String mediaType, Header[] headers) {
        return doFormPost(url, params, mediaType, DEFAULT_ENCODING, headers);
    }

    public static String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding) {
        return doFormPost(url, params, mediaType, encoding, DEFAULT_TIME_OUT);
    }

    public static String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
                                    Header[] headers) {
        return doFormPost(url, params, mediaType, encoding, DEFAULT_TIME_OUT, headers);
    }

    public static String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
                                    int timeoutMilliSeconds) {
        return doFormPost(url, params, mediaType, encoding, timeoutMilliSeconds, null);
    }

    public static String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
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
        log.warn("调用服务失败, 服务地址: " + url, t);
        throw new CommunicationException("调用服务失败, 服务地址: " + url + ", 异常类型: " + t.getClass() + ", 错误原因: " + t.getMessage());
    }

    private static void closeQuietly(CloseableHttpResponse httpResponse) {
        if (Objects.nonNull(httpResponse)) {
            try {
                httpResponse.close();
            } catch (IOException e) {
                log.error("关闭http response失败", e);
            }
        }
    }

    private static void closeIdleExpiredConnections(PoolingHttpClientConnectionManager connectionManager) {
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1,
                LyThreadFactoryBuilderUtil.build("http-client-idle"));
        service.scheduleAtFixedRate(() -> {
            try {
                connectionManager.closeExpiredConnections();
                connectionManager.closeIdleConnections(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("close expired/idle connections exception", e);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
}
