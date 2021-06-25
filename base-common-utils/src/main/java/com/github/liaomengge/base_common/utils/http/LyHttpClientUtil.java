package com.github.liaomengge.base_common.utils.http;

import com.github.liaomengge.base_common.helper.rest.sync.interceptor.HttpHeaderInterceptor;
import com.github.liaomengge.base_common.helper.rest.sync.retry.HttpRetryHandler;
import com.github.liaomengge.base_common.support.exception.CommunicationException;
import com.github.liaomengge.base_common.utils.log.LyAlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

/**
 * 高并发调用时, 不建议使用
 * Created by liaomengge on 16/12/13.
 */
@Slf4j
public class LyHttpClientUtil {
    
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String DEFAULT_MEDIA_TYPE_JSON = "application/json";
    private static final String DEFAULT_MEDIA_TYPE_FORM = "application/x-www-form-urlencoded";

    private static final int DEFAULT_RETRY_TIMES = 3;//总请求次数,重试2次
    private static final int DEFAULT_TIME_OUT = 5_000;//默认5秒超时

    /*****************************************************************
     * GET
     ***************************************************************/

    public static String get(String url) {
        return get(url, DEFAULT_ENCODING, null, DEFAULT_RETRY_TIMES);
    }

    public static String get(String url, Header[] headers) {
        return get(url, DEFAULT_ENCODING, headers, DEFAULT_RETRY_TIMES);
    }

    public static String get(String url, int timeoutMilliSeconds) {
        return get(url, timeoutMilliSeconds, null, DEFAULT_RETRY_TIMES);
    }

    public static String get(String url, int timeoutMilliSeconds, Header[] headers) {
        return get(url, timeoutMilliSeconds, headers, DEFAULT_RETRY_TIMES);
    }

    public static String get(String url, String encoding, int reTryTimes) {
        return get(url, encoding, null, reTryTimes);
    }

    public static String get(String url, String encoding, Header[] headers, int reTryTimes) {
        return get(url, encoding, DEFAULT_TIME_OUT, headers, reTryTimes);
    }

    public static String get(String url, int timeoutMilliSeconds, int reTryTimes) {
        return get(url, timeoutMilliSeconds, null, reTryTimes);
    }

    public static String get(String url, int timeoutMilliSeconds, Header[] headers, int reTryTimes) {
        return get(url, "utf-8", timeoutMilliSeconds, headers, reTryTimes);
    }

    public static String get(String url, String encoding, int timeoutMilliSeconds, int reTryTimes) {
        return get(url, encoding, timeoutMilliSeconds, null, reTryTimes);
    }

    /**
     * Get请求
     * <p>
     * 如果异常, throw {@link CommunicationException}
     *
     * @param url
     * @param encoding
     * @param timeoutMilliSeconds
     * @param reTryTimes
     * @return
     */
    public static String get(String url, String encoding, int timeoutMilliSeconds, Header[] headers, int reTryTimes) {
        String result = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient =
                    HttpClients.custom().setRetryHandler(new HttpRetryHandler(reTryTimes)).addInterceptorFirst(new HttpHeaderInterceptor()).build();
            RequestConfig config =
                    RequestConfig.custom().setConnectTimeout(timeoutMilliSeconds).setSocketTimeout(timeoutMilliSeconds).setConnectionRequestTimeout(timeoutMilliSeconds).build();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(config);
            if (headers != null && headers.length > 0) {
                httpGet.setHeaders(headers);
            }
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            try {
                result = EntityUtils.toString(httpResponse.getEntity(), encoding);
            } finally {
                httpResponse.close();
            }
        } catch (Throwable t) {
            handleThrowable(t, url);
        } finally {
            closeQuietly(httpClient);
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
        return post(url, postData, timeoutMilliSeconds, header, DEFAULT_RETRY_TIMES);
    }

    public static String post(String url, String postData, int timeoutMilliSeconds, int reTryTimes) {
        return post(url, postData, timeoutMilliSeconds, null, reTryTimes);
    }

    public static String post(String url, String postData, int timeoutMilliSeconds, Header[] header, int reTryTimes) {
        return post(url, postData, DEFAULT_MEDIA_TYPE_JSON, DEFAULT_ENCODING, timeoutMilliSeconds, header, reTryTimes);
    }

    public static String post(String url, String postData, String mediaType, int reTryTimes) {
        return post(url, postData, mediaType, DEFAULT_ENCODING, null, reTryTimes);
    }

    public static String post(String url, String postData, String mediaType, Header[] header, int reTryTimes) {
        return post(url, postData, mediaType, DEFAULT_ENCODING, header, reTryTimes);
    }

    public static String post(String url, String postData, String mediaType, String encoding, int reTryTimes) {
        return post(url, postData, mediaType, encoding, null, reTryTimes);
    }

    public static String post(String url, String postData, String mediaType, String encoding, Header[] headers,
                              int reTryTimes) {
        return post(url, postData, mediaType, encoding, DEFAULT_TIME_OUT, headers, reTryTimes);
    }

    /**
     * Post Json请求
     * <p>
     * 如果异常, throw {@link CommunicationException}
     *
     * @param url
     * @param postData
     * @param mediaType
     * @param encoding
     * @param headers
     * @param timeoutMilliSeconds
     * @param reTryTimes
     * @return
     */
    public static String post(String url, String postData, String mediaType, String encoding, int timeoutMilliSeconds
            , Header[] headers, int reTryTimes) {
        String result = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient =
                    HttpClients.custom().setRetryHandler(new HttpRetryHandler(reTryTimes)).addInterceptorFirst(new HttpHeaderInterceptor()).build();
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
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            try {
                result = EntityUtils.toString(httpResponse.getEntity(), encoding);
            } finally {
                httpResponse.close();
            }
        } catch (Throwable t) {
            handleThrowable(t, url);
        } finally {
            closeQuietly(httpClient);
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
                headers,
                DEFAULT_RETRY_TIMES);
    }

    public static String doFormPost(String url, List<NameValuePair> params, int timeoutMilliSeconds, int reTryTimes) {
        return doFormPost(url, params, timeoutMilliSeconds, null, reTryTimes);
    }

    public static String doFormPost(String url, List<NameValuePair> params, int timeoutMilliSeconds, Header[] headers,
                                    int reTryTimes) {
        return doFormPost(url, params, DEFAULT_MEDIA_TYPE_FORM, DEFAULT_ENCODING, timeoutMilliSeconds,
                headers,
                reTryTimes);
    }

    public static String doFormPost(String url, List<NameValuePair> params, String mediaType) {
        return doFormPost(url, params, mediaType, DEFAULT_ENCODING);
    }

    public static String doFormPost(String url, List<NameValuePair> params, String mediaType, Header[] headers) {
        return doFormPost(url, params, mediaType, DEFAULT_ENCODING, headers);
    }

    public static String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding) {
        return doFormPost(url, params, mediaType, encoding, DEFAULT_TIME_OUT, DEFAULT_RETRY_TIMES);
    }

    public static String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
                                    Header[] headers) {
        return doFormPost(url, params, mediaType, encoding, DEFAULT_TIME_OUT, headers, DEFAULT_RETRY_TIMES);
    }

    public static String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
                                    int timeoutMilliSeconds, int reTryTimes) {
        return doFormPost(url, params, mediaType, encoding, timeoutMilliSeconds, null, reTryTimes);
    }

    /**
     * Post Form请求
     * <p>
     * 如果异常, throw {@link CommunicationException}
     *
     * @param url
     * @param params
     * @param mediaType
     * @param encoding
     * @param timeoutMilliSeconds
     * @param reTryTimes
     * @return
     */
    public static String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
                                    int timeoutMilliSeconds, Header[] headers, int reTryTimes) {
        String result = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient =
                    HttpClients.custom().setRetryHandler(new HttpRetryHandler(reTryTimes)).addInterceptorFirst(new HttpHeaderInterceptor()).build();
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
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            try {
                result = EntityUtils.toString(httpResponse.getEntity(), encoding);
            } finally {
                httpResponse.close();
            }
        } catch (Throwable t) {
            handleThrowable(t, url);
        } finally {
            closeQuietly(httpClient);
        }
        return result;
    }

    /*****************************************************************
     * POST File
     ***************************************************************/
    public static String doFilePost(String url, MultipartEntityBuilder builder, String encoding,
                                    int timeoutMilliSeconds) {
        String result = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.custom().build();
            RequestConfig config =
                    RequestConfig.custom().setConnectTimeout(timeoutMilliSeconds).setSocketTimeout(timeoutMilliSeconds).setConnectionRequestTimeout(timeoutMilliSeconds).build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(config);

            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            try {
                result = EntityUtils.toString(httpResponse.getEntity(), encoding);
            } finally {
                httpResponse.close();
            }
        } catch (Throwable t) {
            handleThrowable(t, url);
        } finally {
            closeQuietly(httpClient);
        }
        return result;
    }

    public static String doFilePost(String url, String fileName, InputStream inputStream, String filePath) {
        return doFilePost(url, fileName, inputStream, filePath, Consts.UTF_8.name(), DEFAULT_TIME_OUT);
    }

    public static String doFilePost(String url, String fileName, InputStream inputStream, String filePath,
                                    int timeoutMilliSeconds) {
        return doFilePost(url, fileName, inputStream, filePath, Consts.UTF_8.name(), timeoutMilliSeconds);
    }

    /**
     * @param url
     * @param fileName
     * @param inputStream         上传的文件流
     * @param filePath            上传的文件路径
     * @param encoding
     * @param timeoutMilliSeconds
     * @return
     */
    public static String doFilePost(String url, String fileName, InputStream inputStream, String filePath,
                                    String encoding, int timeoutMilliSeconds) {
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addTextBody("filePath", filePath, ContentType.create("text/plain", Charset.forName(encoding)));
            builder.addBinaryBody("uploadFile", inputStream, ContentType.DEFAULT_BINARY, fileName);

            return doFilePost(url, builder, encoding, timeoutMilliSeconds);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("close http client inputStream fail", e);
                }
            }
        }
    }

    private static void handleThrowable(Throwable t, String url) {
        if (t instanceof InterruptedIOException || (Objects.nonNull(t.getCause()) && t.getCause() instanceof InterruptedIOException)) {
            LyAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_HTTP.error(t);
        } else {
            LyAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_BIZ.error(t);
        }
        log.warn("call service fail, url: " + url, t);
        throw new CommunicationException("http call fail, url=> " + url, t);
    }

    private static void closeQuietly(CloseableHttpClient httpClient) {
        if (Objects.nonNull(httpClient)) {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("close http client fail", e);
            }
        }
    }
}
