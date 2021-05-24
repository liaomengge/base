package com.github.liaomengge.base_common.utils.web;

import com.github.liaomengge.base_common.support.misc.Charsets;
import com.github.liaomengge.base_common.utils.collection.LyListUtil;
import com.github.liaomengge.base_common.utils.collection.LyMoreCollectionUtil;
import com.github.liaomengge.base_common.utils.io.LyIOUtil;
import com.github.liaomengge.base_common.utils.json.LyJacksonUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.JOINER;

/**
 * Created by liaomengge on 17/11/7.
 */
@UtilityClass
public class LyWebUtil {

    public final Logger log = LyLogger.getInstance(LyWebUtil.class);

    public Optional<ServletRequestAttributes> getRequestAttributes() {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Optional.ofNullable(requestAttributes);
    }

    public Optional<HttpServletRequest> getHttpServletRequest() {
        return getRequestAttributes().map(ServletRequestAttributes::getRequest);
    }

    public Optional<HttpServletResponse> getHttpServletResponse() {
        return getRequestAttributes().map(ServletRequestAttributes::getResponse);
    }

    public Map<String, Object> getRequestAttributes(HttpServletRequest request) {
        Map<String, Object> attributeMap = Maps.newHashMap();
        Enumeration<String> attributeNames = request.getAttributeNames();
        if (Objects.nonNull(attributeNames)) {
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                if (StringUtils.isNoneBlank(name)) {
                    Object value = request.getAttribute(name);
                    attributeMap.put(name, value);
                }
            }
        }
        return attributeMap;
    }

    /*******************************************************Header*****************************************************/

    public Map<String, String> getRequestStringHeaders(HttpServletRequest request) {
        return getRequestHeaders(request, val -> LyListUtil.getFirst(EnumerationUtils.toList(val)));
    }

    public Map<String, List<String>> getRequestListHeaders(HttpServletRequest request) {
        return getRequestHeaders(request, EnumerationUtils::toList);
    }

    /**
     * 获取请求参数
     *
     * @param request
     * @param function
     * @param <V>
     * @return
     */
    public <V> Map<String, V> getRequestHeaders(HttpServletRequest request, Function<Enumeration<String>, V> function) {
        Map<String, V> headerMap = new LinkedCaseInsensitiveMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (Objects.nonNull(headerNames)) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                if (StringUtils.isNoneBlank(name)) {
                    Enumeration<String> value = request.getHeaders(name);
                    if (Objects.isNull(value)) {
                        headerMap.put(name, null);
                    } else {
                        headerMap.put(name, function.apply(value));
                    }
                }
            }
        }
        return headerMap;
    }

    /*******************************************************Params*****************************************************/

    /**
     * 获取Get/Post请求参数(文件上传除外)
     *
     * @param request
     * @return
     */
    public Map<String, String[]> getRequestArrayParams(HttpServletRequest request) {
        return getRequestParams(request, Function.identity());
    }

    /**
     * 获取Get/Post请求参数(文件上传除外)
     *
     * @param request
     * @return
     */
    public Map<String, List<String>> getRequestListParams(HttpServletRequest request) {
        return getRequestParams(request, LyMoreCollectionUtil::toList);
    }

    /**
     * 获取Get/Post请求参数(文件上传除外)
     *
     * @param request
     * @return
     */
    public Map<String, String> getRequestStringParams(HttpServletRequest request) {
        return getRequestParams(request, JOINER::join);
    }

    /**
     * 获取Get/Post请求参数(文件上传除外)
     *
     * @param request
     * @param function
     * @param <V>
     * @return
     */
    public <V> Map<String, V> getRequestParams(HttpServletRequest request, Function<String[], V> function) {
        Map<String, V> parameterMap = Maps.newHashMap();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (StringUtils.isNoneBlank(paramName)) {
                String[] values = request.getParameterValues(paramName);
                if (ArrayUtils.isEmpty(values)) {
                    parameterMap.put(paramName, null);
                } else {
                    parameterMap.put(paramName, function.apply(values));
                }
            }
        }
        return parameterMap;
    }

    /*******************************************************Render*****************************************************/

    /**
     * 获取请求的json body
     *
     * @param request
     * @return
     */
    public String getReqJson(HttpServletRequest request) {
        return getReqJson(request, Charsets.UTF_8.name());
    }

    /**
     * 获取请求的json body(只能获取'application/json;charset=UTF-8'的数据)
     *
     * @param request
     * @return
     */
    public String getReqJson(HttpServletRequest request, String contentType) {
        try {
            request.setCharacterEncoding(contentType);
        } catch (UnsupportedEncodingException e) {
            log.warn("unsupported contentType[" + contentType + "]", e);
        }
        try {
            return LyIOUtil.toString(request.getReader());
        } catch (IOException e) {
            log.error("read request stream fail", e);
        }
        return "";
    }

    /**
     * 返回json
     *
     * @param response HttpServletResponse
     * @param result   结果对象
     */
    public void renderJson(HttpServletResponse response, Object result) {
        renderJson(response, result, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 返回json
     *
     * @param response    HttpServletResponse
     * @param result      结果对象
     * @param contentType contentType
     */
    public void renderJson(HttpServletResponse response, Object result, String contentType) {
        response.setCharacterEncoding(Charsets.UTF_8.name());
        response.setContentType(contentType);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(LyJacksonUtil.toJson(result));
            writer.flush();
        } catch (Exception e) {
            log.error("write response stream exception", e);
        }
    }

    /*******************************************************Other*****************************************************/

    /**
     * 是否为GET请求
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 是否为GET请求
     */
    public boolean isGetMethod(HttpServletRequest request) {
        return "GET".equalsIgnoreCase(request.getMethod());
    }

    /**
     * 是否为POST请求
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 是否为POST请求
     */
    public boolean isPostMethod(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod());
    }

    /**
     * 是否为Multipart类型表单, 此类型表单用于文件上传
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 是否为Multipart类型表单, 此类型表单用于文件上传
     */
    public boolean isMultipart(HttpServletRequest request) {
        if (!isPostMethod(request)) {
            return false;
        }

        String contentType = request.getContentType();
        if (StringUtils.isBlank(contentType)) {
            return false;
        }
        if (contentType.toLowerCase().startsWith("multipart/")) {
            return true;
        }

        return false;
    }
}
