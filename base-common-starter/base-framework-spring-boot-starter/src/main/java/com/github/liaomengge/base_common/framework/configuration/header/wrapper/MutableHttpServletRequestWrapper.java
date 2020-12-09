package com.github.liaomengge.base_common.framework.configuration.header.wrapper;


import com.github.liaomengge.base_common.utils.collection.LyListUtil;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * Created by liaomengge on 2020/12/8.
 * 解决多重异步调用会丢失header[RequestContextHolder.getRequestAttributes()]
 */
public class MutableHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, List<String>> headers;

    public MutableHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.headers = LyWebUtil.getRequestListHeaders(request);
    }

    @Override
    public String getHeader(String name) {
        List<String> value = headers.get(name);
        if (CollectionUtils.isNotEmpty(value)) {
            return LyListUtil.getFirst(value);
        }
        return null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> value = headers.get(name);
        if (CollectionUtils.isNotEmpty(value)) {
            return Collections.enumeration(value);
        }
        return Collections.enumeration(Lists.newArrayList());
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> value = headers.keySet();
        if (CollectionUtils.isNotEmpty(value)) {
            return Collections.enumeration(value);
        }
        return Collections.enumeration(Lists.newArrayList());
    }

    public void putHeader(String name, String value) {
        this.headers.put(name, Lists.newArrayList(value));
    }

    public void putHeaders(String name, List<String> value) {
        this.headers.put(name, value);
    }
}
