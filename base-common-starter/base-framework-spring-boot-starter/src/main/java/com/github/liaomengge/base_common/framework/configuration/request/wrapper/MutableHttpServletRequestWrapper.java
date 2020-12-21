package com.github.liaomengge.base_common.framework.configuration.request.wrapper;


import com.github.liaomengge.base_common.utils.collection.LyListUtil;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.collections4.MapUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaomengge on 2020/12/8.
 * 解决多重异步调用会丢失header和attribute[RequestContextHolder.getRequestAttributes()]
 */
public class MutableHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, List<String>> headerMap;
    private Map<String, Object> attributeMap;

    public MutableHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.headerMap = LyWebUtil.getRequestListHeaders(request);
        this.attributeMap = new ConcurrentHashMap<>(LyWebUtil.getRequestAttributes(request));
    }

    @Override
    public String getHeader(String name) {
        return LyListUtil.getFirst(MapUtils.getObject(headerMap, name, Lists.newArrayList()));
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Collections.enumeration(MapUtils.getObject(this.headerMap, name, Lists.newArrayList()));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(this.headerMap.keySet());
    }

    @Override
    public Object getAttribute(String name) {
        return MapUtils.getObject(this.attributeMap, name, super.getAttribute(name));
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> attributeNames = this.attributeMap.keySet();
        Enumeration<String> enumeration = super.getAttributeNames();
        if (Objects.nonNull(enumeration)) {
            attributeNames.addAll(EnumerationUtils.toList(enumeration));
        }
        return Collections.enumeration(attributeNames);
    }

    @Override
    public void setAttribute(String name, Object obj) {
        this.attributeMap.put(name, obj);
        super.setAttribute(name, obj);
    }
}
