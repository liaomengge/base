package com.github.liaomengge.base_common.feign.interceptor;

import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Enumeration;

/**
 * Created by liaomengge on 2020/8/25.
 */
public class HeaderRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        LyWebUtil.getHttpServletRequest().ifPresent(request -> {
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String value = request.getHeader(name);
                    requestTemplate.header(name, value);
                }
            }
        });
    }
}
