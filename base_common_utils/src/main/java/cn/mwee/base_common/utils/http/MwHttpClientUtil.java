package cn.mwee.base_common.utils.http;

import cn.mwee.base_common.helper.rest.sync.interceptor.HttpHeaderInterceptor;
import cn.mwee.base_common.helper.rest.sync.retry.HttpRetryHandler;
import cn.mwee.base_common.support.exception.CommunicationException;
import cn.mwee.base_common.utils.log.MwAlarmLogUtil;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import lombok.experimental.UtilityClass;
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
import org.slf4j.Logger;

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
@UtilityClass
public class MwHttpClientUtil {

    private final Logger logger = MwLogger.getInstance(MwHttpClientUtil.class);

    private int RETRY_TIMES = 3;//重试3次
    private int DEFAULT_TIME_OUT = 5_000;//默认5秒超时

    /*****************************************************************
     * GET
     ***************************************************************/

    public String get(String url) {
        return get(url, "utf-8", null, RETRY_TIMES);
    }

    public String get(String url, Header[] headers) {
        return get(url, "utf-8", headers, RETRY_TIMES);
    }

    public String get(String url, int timeoutMilliSeconds) {
        return get(url, timeoutMilliSeconds, null, RETRY_TIMES);
    }

    public String get(String url, int timeoutMilliSeconds, Header[] headers) {
        return get(url, timeoutMilliSeconds, headers, RETRY_TIMES);
    }

    public String get(String url, String encoding, int reTryTimes) {
        return get(url, encoding, null, reTryTimes);
    }

    public String get(String url, String encoding, Header[] headers, int reTryTimes) {
        return get(url, encoding, DEFAULT_TIME_OUT, headers, reTryTimes);
    }

    public String get(String url, int timeoutMilliSeconds, int reTryTimes) {
        return get(url, timeoutMilliSeconds, null, reTryTimes);
    }

    public String get(String url, int timeoutMilliSeconds, Header[] headers, int reTryTimes) {
        return get(url, "utf-8", timeoutMilliSeconds, headers, reTryTimes);
    }

    public String get(String url, String encoding, int timeoutMilliSeconds, int reTryTimes) {
        return get(url, encoding, timeoutMilliSeconds, null, reTryTimes);
    }

    /**
     * Get请求
     * <p>
     * 如果异常, throw {@link cn.mwee.base_common.support.exception.CommunicationException}
     *
     * @param url
     * @param encoding
     * @param timeoutMilliSeconds
     * @param reTryTimes
     * @return
     */
    public String get(String url, String encoding, int timeoutMilliSeconds, Header[] headers, int reTryTimes) {
        String result = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient =
                    HttpClients.custom().setRetryHandler(new HttpRetryHandler(reTryTimes)).addInterceptorFirst(new HttpHeaderInterceptor()).build();
            RequestConfig config =
                    RequestConfig.custom().setConnectTimeout(timeoutMilliSeconds).setSocketTimeout(timeoutMilliSeconds).setConnectionRequestTimeout(timeoutMilliSeconds).build();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(config);
            if (headers != null && headers.length > 0) httpGet.setHeaders(headers);
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
        return post(url, postData, timeoutMilliSeconds, header, RETRY_TIMES);
    }

    public String post(String url, String postData, int timeoutMilliSeconds, int reTryTimes) {
        return post(url, postData, timeoutMilliSeconds, null, reTryTimes);
    }

    public String post(String url, String postData, int timeoutMilliSeconds, Header[] header, int reTryTimes) {
        return post(url, postData, "application/json", "utf-8", timeoutMilliSeconds, header, reTryTimes);
    }

    public String post(String url, String postData, String mediaType, int reTryTimes) {
        return post(url, postData, mediaType, "utf-8", null, reTryTimes);
    }

    public String post(String url, String postData, String mediaType, Header[] header, int reTryTimes) {
        return post(url, postData, mediaType, "utf-8", header, reTryTimes);
    }

    public String post(String url, String postData, String mediaType, String encoding, int reTryTimes) {
        return post(url, postData, mediaType, encoding, null, reTryTimes);
    }

    public String post(String url, String postData, String mediaType, String encoding, Header[] headers,
                       int reTryTimes) {
        return post(url, postData, mediaType, encoding, DEFAULT_TIME_OUT, headers, reTryTimes);
    }

    /**
     * Post Json请求
     * <p>
     * 如果异常, throw {@link cn.mwee.base_common.support.exception.CommunicationException}
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
    public String post(String url, String postData, String mediaType, String encoding, int timeoutMilliSeconds
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
            if (headers != null && headers.length > 0) httpPost.setHeaders(headers);
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

    public String doFormPost(String url, List<NameValuePair> params, int timeoutMilliSeconds) {
        return doFormPost(url, params, timeoutMilliSeconds, null);
    }

    public String doFormPost(String url, List<NameValuePair> params, int timeoutMilliSeconds, Header[] headers) {
        return doFormPost(url, params, "application/x-www-form-urlencoded", "utf-8", timeoutMilliSeconds, headers,
                RETRY_TIMES);
    }

    public String doFormPost(String url, List<NameValuePair> params, int timeoutMilliSeconds, int reTryTimes) {
        return doFormPost(url, params, timeoutMilliSeconds, null, reTryTimes);
    }

    public String doFormPost(String url, List<NameValuePair> params, int timeoutMilliSeconds, Header[] headers,
                             int reTryTimes) {
        return doFormPost(url, params, "application/x-www-form-urlencoded", "utf-8", timeoutMilliSeconds, headers,
                reTryTimes);
    }

    public String doFormPost(String url, List<NameValuePair> params, String mediaType) {
        return doFormPost(url, params, mediaType, "utf-8");
    }

    public String doFormPost(String url, List<NameValuePair> params, String mediaType, Header[] headers) {
        return doFormPost(url, params, mediaType, "utf-8", headers);
    }

    public String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding) {
        return doFormPost(url, params, mediaType, encoding, DEFAULT_TIME_OUT, RETRY_TIMES);
    }

    public String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
                             Header[] headers) {
        return doFormPost(url, params, mediaType, encoding, DEFAULT_TIME_OUT, headers, RETRY_TIMES);
    }

    public String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
                             int timeoutMilliSeconds, int reTryTimes) {
        return doFormPost(url, params, mediaType, encoding, timeoutMilliSeconds, null, reTryTimes);
    }

    /**
     * Post Form请求
     * <p>
     * 如果异常, throw {@link cn.mwee.base_common.support.exception.CommunicationException}
     *
     * @param url
     * @param params
     * @param mediaType
     * @param encoding
     * @param timeoutMilliSeconds
     * @param reTryTimes
     * @return
     */
    public String doFormPost(String url, List<NameValuePair> params, String mediaType, String encoding,
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
            if (headers != null && headers.length > 0) httpPost.setHeaders(headers);
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
    public String doFilePost(String url, MultipartEntityBuilder builder, String encoding,
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

    public String doFilePost(String url, String fileName, InputStream inputStream, String filePath) {
        return doFilePost(url, fileName, inputStream, filePath, Consts.UTF_8.name(), DEFAULT_TIME_OUT);
    }

    public String doFilePost(String url, String fileName, InputStream inputStream, String filePath,
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
    public String doFilePost(String url, String fileName, InputStream inputStream, String filePath,
                             String encoding, int timeoutMilliSeconds) {
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addTextBody("filePath", filePath, ContentType.create("text/plain", Charset.forName(encoding)));
            builder.addBinaryBody("uploadFile", inputStream, ContentType.DEFAULT_BINARY, fileName);

            return doFilePost(url, builder, encoding, timeoutMilliSeconds);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                logger.error("关闭http client inputStream失败", e);
            }
        }
    }

    private static void handleThrowable(Throwable t, String url) {
        if (t instanceof InterruptedIOException || (Objects.nonNull(t.getCause()) && t.getCause() instanceof InterruptedIOException))
            MwAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_HTTP.error(t);
        else
            MwAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_BIZ.error(t);
        logger.warn("调用服务失败, 服务地址: " + url, t);
        throw new CommunicationException("调用服务失败, 服务地址: " + url + ", 异常类型: " + t.getClass() + ", 错误原因: " + t.getMessage());
    }

    private void closeQuietly(CloseableHttpClient httpClient) {
        if (Objects.nonNull(httpClient)) try {
            httpClient.close();
        } catch (IOException e) {
            logger.error("关闭http client失败", e);
        }
    }
}
