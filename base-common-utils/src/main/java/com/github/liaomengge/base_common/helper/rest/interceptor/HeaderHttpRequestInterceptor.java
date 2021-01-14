package com.github.liaomengge.base_common.helper.rest.interceptor;

import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by liaomengge on 2021/1/14.
 */
public class HeaderHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders httpHeaders = httpRequest.getHeaders();
        LyWebUtil.getHttpServletRequest().ifPresent(request -> {
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String value = request.getHeader(name);
                    if (StringUtils.isNotBlank(name)) {
                        httpHeaders.add(name, value);
                    }
                }
            }
        });
        return execution.execute(httpRequest, bytes);
    }
}
