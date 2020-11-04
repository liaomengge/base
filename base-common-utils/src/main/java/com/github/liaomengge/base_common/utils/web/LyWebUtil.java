package com.github.liaomengge.base_common.utils.web;

import com.github.liaomengge.base_common.support.misc.Charsets;
import com.github.liaomengge.base_common.utils.collection.LyMoreCollectionUtil;
import com.github.liaomengge.base_common.utils.io.LyIOUtil;
import com.github.liaomengge.base_common.utils.json.LyJacksonUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.JOINER;

/**
 * Created by liaomengge on 17/11/7.
 */
@UtilityClass
public class LyWebUtil {

    public final Logger log = LyLogger.getInstance(Charsets.class);

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

    public Map<String, String> getRequestHeaders(HttpServletRequest servletRequest) {
        Map<String, String> headerMap = Maps.newHashMap();
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        if (Objects.nonNull(headerNames)) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = servletRequest.getHeader(name);
                headerMap.put(name, value);
            }
        }
        return headerMap;
    }

    /**
     * 获取Get/Post请求参数(文件上传除外)
     *
     * @param servletRequest
     * @param function
     * @param <V>
     * @return
     */
    public <V> Map<String, V> getRequestParams(HttpServletRequest servletRequest, Function<String[], V> function) {
        Map<String, V> parameterMap = Maps.newHashMap();
        Enumeration<String> paramNames = servletRequest.getParameterNames();
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] values = servletRequest.getParameterValues(paramName);
            if (values == null || values.length == 0) {
                parameterMap.put(paramName, null);
            } else {
                parameterMap.put(paramName, function.apply(values));
            }
        }
        return parameterMap;
    }

    /**
     * 获取Get/Post请求参数(文件上传除外)
     *
     * @param servletRequest
     * @return
     */
    public Map<String, String[]> getRequestParams(HttpServletRequest servletRequest) {
        return getRequestParams(servletRequest, Function.identity());
    }

    /**
     * 获取Get/Post请求参数(文件上传除外)
     *
     * @param servletRequest
     * @return
     */
    public Map<String, List<String>> getRequestMultiParams(HttpServletRequest servletRequest) {
        return getRequestParams(servletRequest, LyMoreCollectionUtil::toList);
    }

    /**
     * 获取Get/Post请求参数(文件上传除外)
     *
     * @param servletRequest
     * @return
     */
    public Map<String, String> getRequestStringParams(HttpServletRequest servletRequest) {
        return getRequestParams(servletRequest, JOINER::join);
    }

    /**
     * 获取切面请求信息
     *
     * @param method
     * @param args
     * @return
     */
    public Object getRequestParams(Method method, Object[] args) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (ArrayUtils.isEmpty(args) || ArrayUtils.isEmpty(parameterAnnotations)) {
            return null;
        }
        if (args.length == 1) {
            if (args[0] instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) args[0];
                return getRequestParams(request);
            }
            if (args[0] instanceof WebRequest) {
                WebRequest request = (WebRequest) args[0];
                return request.getParameterMap();
            }
        }
        List<Object> parameterList = Lists.newArrayList();
        for (int i = 0; i < parameterAnnotations.length && i < args.length; i++) {
            if (validateAnnotation(parameterAnnotations[i])) {
                parameterList.add(args[i]);
            } else {
                parameterList.add(convertStreamArg(args[i]));
            }
        }
        return parameterList;
    }

    private boolean validateAnnotation(Annotation[] annotations) {
        return Arrays.stream(annotations).anyMatch(annotation ->
                annotation instanceof RequestBody || annotation instanceof RequestParam
                        || annotation instanceof CookieValue || annotation instanceof PathVariable
                        || annotation instanceof ModelAttribute || annotation instanceof RequestAttribute
                        || annotation instanceof RequestHeader || annotation instanceof SessionAttribute
        );
    }

    private String convertStreamArg(Object arg) {
        if (arg instanceof InputStream || arg instanceof InputStreamSource) {
            return "[Binary data]";
        }
        if (arg instanceof ServletRequest) {
            return "[ServletRequest]";
        }
        if (arg instanceof WebRequest) {
            return "[WebRequest]";
        }
        if (arg instanceof ServletResponse) {
            return "[ServletResponse]";
        }
        if (arg instanceof Model) {
            return "[Model]";
        }
        if (arg instanceof BindingResult) {
            return "[BindingResult]";
        }
        return Objects.toString(arg, "NULL");
    }

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
        if (false == isPostMethod(request)) {
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
