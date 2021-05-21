package com.github.liaomengge.base_common.framework.configuration.request.wrapper;


import com.github.liaomengge.base_common.utils.collection.LyListUtil;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaomengge on 2020/12/8.
 * 解决多重异步调用会丢失header和attribute[RequestContextHolder.getRequestAttributes()]
 */
public class MutableHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private String requestURI;
    private StringBuffer requestURL;
    private Map<String, List<String>> headerMap;
    private Map<String, Object> attributeMap;

    public MutableHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.requestURI = request.getRequestURI();
        this.requestURL = request.getRequestURL();
        this.headerMap = new ConcurrentHashMap<>(LyWebUtil.getRequestListHeaders(request));
        this.attributeMap = new ConcurrentHashMap<>(LyWebUtil.getRequestAttributes(request));
    }

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    @Override
    public StringBuffer getRequestURL() {
        return requestURL;
    }

    @Override
    public String getHeader(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return LyListUtil.getFirst(MapUtils.getObject(headerMap, name, Lists.newArrayList()));
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (StringUtils.isBlank(name)) {
            return Collections.enumeration(Lists.newArrayList());
        }
        return Collections.enumeration(MapUtils.getObject(this.headerMap, name, Lists.newArrayList()));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(this.headerMap.keySet());
    }

    public void addHeader(String name, String value) {
        if (StringUtils.isNotBlank(name)) {
            this.headerMap.put(name, Lists.newArrayList(value));
        }
    }

    public void addHeader(String name, List<String> values) {
        if (StringUtils.isNotBlank(name)) {
            this.headerMap.put(name, values);
        }
    }

    @Override
    public Object getAttribute(String name) {
        return MapUtils.getObject(this.attributeMap, name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(this.attributeMap.keySet());
    }

    @Override
    public void setAttribute(String name, Object obj) {
        if (Objects.nonNull(name) && Objects.nonNull(obj)) {
            this.attributeMap.put(name, obj);
        }
    }

    @Override
    public void removeAttribute(String name) {
        if (Objects.nonNull(name)) {
            attributeMap.remove(name);
        }
    }
}
