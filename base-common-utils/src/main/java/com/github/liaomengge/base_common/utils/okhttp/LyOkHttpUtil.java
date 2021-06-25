package com.github.liaomengge.base_common.utils.okhttp;

import com.github.liaomengge.base_common.support.exception.CommunicationException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2020/8/6.
 */
@Slf4j
public class LyOkHttpUtil {
    
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static final int RETRY_TIME = 2;//重试次数

    private static final int CONNECTION_TIME_OUT = 5_000;//连接超时时间
    private static final int SOCKET_TIME_OUT = 5_000;//读写超时时间

    private static final int MAX_IDLE_CONNECTIONS = 30;// 空闲连接数
    private static final long KEEP_ALIVE_TIME = 60_000L;//保持连接时间

    private static OkHttpClient okHttpClient;

    static {
        ConnectionPool connectionPool = new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(SOCKET_TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(SOCKET_TIME_OUT, TimeUnit.MILLISECONDS)
                .connectionPool(connectionPool)
                .addInterceptor(new RetryInterceptor(RETRY_TIME))
                .build();
    }

    public static String get(String url) {
        return get(url, new HashMap<>(), new HashMap<>());
    }

    public static String get(String url, Map<String, String> pathParams) {
        return get(url, pathParams, new HashMap<>());
    }

    public static String get(String url, Map<String, String> pathParams, Map<String, String> headers) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        pathParams.forEach((key, value) -> httpUrlBuilder.addQueryParameter(key, value));

        Request.Builder builder = new Request.Builder();
        headers.forEach((String key, String value) -> builder.header(key, value));
        Request request = builder.url(httpUrlBuilder.build().toString()).build();
        return execute(request, url);
    }

    public static String post(String url) {
        return post(url, "");
    }

    public static String post(String url, String body) {
        return post(url, body, new HashMap<>());
    }

    public static String post(String url, String body, Map<String, String> headers) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, body);

        Request.Builder builder = new Request.Builder();
        headers.forEach((String key, String value) -> builder.header(key, value));

        Request request = builder.post(requestBody).url(httpUrlBuilder.build().toString()).build();
        return execute(request, url);
    }

    public static String doFormPost(String url, Map<String, String> bodyParams) {
        return doFormPost(url, bodyParams, new HashMap<>());
    }

    public static String doFormPost(String url, Map<String, String> bodyParams, Map<String, String> headers) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        bodyParams.forEach((key, value) -> formBodyBuilder.add(key, value));

        Request.Builder builder = new Request.Builder();
        headers.forEach((String key, String value) -> builder.header(key, value));

        Request request = builder.post(formBodyBuilder.build()).url(httpUrlBuilder.build().toString()).build();
        return execute(request, url);
    }

    /***************************************************异步调用*****************************************************/

    public static void get(String url, Callback callback) {
        get(url, new HashMap<>(), new HashMap<>(), callback);
    }

    public static void get(String url, Map<String, String> pathParams, Callback callback) {
        get(url, pathParams, new HashMap<>(), callback);
    }

    public static void get(String url, Map<String, String> pathParams, Map<String, String> headers, Callback callback) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        pathParams.forEach((key, value) -> httpUrlBuilder.addQueryParameter(key, value));

        Request.Builder builder = new Request.Builder();
        headers.forEach((String key, String value) -> builder.header(key, value));
        Request request = builder.url(httpUrlBuilder.build().toString()).build();
        enqueue(request, callback);
    }

    public static void post(String url, Callback callback) {
        post(url, "", callback);
    }

    public static void post(String url, String body, Callback callback) {
        post(url, body, new HashMap<>(), callback);
    }

    public static void post(String url, String body, Map<String, String> headers, Callback callback) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, body);

        Request.Builder builder = new Request.Builder();
        headers.forEach((String key, String value) -> builder.header(key, value));

        Request request = builder.post(requestBody).url(httpUrlBuilder.build().toString()).build();
        enqueue(request, callback);
    }

    public static void doFormPost(String url, Map<String, String> bodyParams, Callback callback) {
        doFormPost(url, bodyParams, new HashMap<>(), callback);
    }

    public static void doFormPost(String url, Map<String, String> bodyParams, Map<String, String> headers,
                                  Callback callback) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        bodyParams.forEach((key, value) -> formBodyBuilder.add(key, value));

        Request.Builder builder = new Request.Builder();
        headers.forEach((String key, String value) -> builder.header(key, value));

        Request request = builder.post(formBodyBuilder.build()).url(httpUrlBuilder.build().toString()).build();
        enqueue(request, callback);
    }

    private static String execute(Request request, String url) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            }
            log.warn("http call fail, [url={}, errorCode = {}, message = {}]", url, response.code(),
                    response.message());
        } catch (Throwable t) {
            log.warn("http call fail, url=> " + url, t);
            throw new CommunicationException("http call fail, url=> " + url, t);
        }
        return null;
    }

    private static void enqueue(Request request, Callback callback) {
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static class RetryInterceptor implements Interceptor {
        private int count = 0;
        private int maxRetryCount;

        public RetryInterceptor(int maxRetryCount) {
            this.maxRetryCount = maxRetryCount;
        }

        @Override
        public Response intercept(Chain chain) {
            return retry(chain);
        }

        public Response retry(Chain chain) {
            Response response = null;
            Request request = chain.request();
            try {
                response = chain.proceed(request);
                while (!response.isSuccessful() && count < maxRetryCount) {
                    count++;
                    response = retry(chain);
                }
            } catch (Exception e) {
                while (count < maxRetryCount) {
                    count++;
                    response = retry(chain);
                }
            }
            return response;
        }
    }
}
