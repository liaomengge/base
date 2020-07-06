package cn.ly.base_common.utils.web;

import cn.ly.base_common.support.misc.Charsets;
import cn.ly.base_common.support.misc.consts.ToolConst;
import cn.ly.base_common.utils.io.LyIOUtil;
import cn.ly.base_common.utils.json.LyJsonUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by liaomengge on 17/11/7.
 */
@UtilityClass
public class LyWebUtil {

    public final Logger logger = LyLogger.getInstance(Charsets.class);

    /**
     * 获取Get/Post请求参数(文件上传除外)
     *
     * @param servletRequest
     * @return
     */
    public Map<String, Object> getRequestParams(HttpServletRequest servletRequest) {
        Map<String, Object> paramMap = new TreeMap<>();
        Enumeration<String> paramNames = servletRequest.getParameterNames();
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] values = servletRequest.getParameterValues(paramName);
            if (values == null || values.length == 0) {
                paramMap.put(paramName, null);
            } else if (values.length > 1) {
                paramMap.put(paramName, values);
            } else {
                paramMap.put(paramName, values[0]);
            }
        }
        return paramMap;
    }

    /**
     * 获取Get/Post Mutil请求参数(文件上传除外)
     *
     * @param servletRequest
     * @return
     */
    public MultivaluedMap<String, String> getRequestMultiParams(HttpServletRequest servletRequest) {
        MultivaluedMap<String, String> multivaluedMap = new MultivaluedHashMap<>();
        Enumeration<String> paramNames = servletRequest.getParameterNames();
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] values = servletRequest.getParameterValues(paramName);
            if (values == null || values.length == 0) {
                multivaluedMap.put(paramName, Lists.newArrayList());
            } else {
                multivaluedMap.put(paramName, Lists.newArrayList(values));
            }
        }
        return multivaluedMap;
    }

    /**
     * 获取Get/Post请求参数(文件上传除外)
     *
     * @param servletRequest
     * @return
     */
    public Map<String, String> getRequestStringParams(HttpServletRequest servletRequest) {
        Map<String, String> paramMap = new TreeMap<>();
        Enumeration<String> paramNames = servletRequest.getParameterNames();
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] values = servletRequest.getParameterValues(paramName);
            if (values == null || values.length == 0) {
                paramMap.put(paramName, null);
            } else {
                paramMap.put(paramName, ToolConst.JOINER.join(values));
            }
        }
        return paramMap;
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
            logger.warn("unsupported contentType[" + contentType + "]", e);
        }
        try {
            return LyIOUtil.toString(request.getReader());
        } catch (IOException e) {
            logger.error("read request stream fail", e);
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
            writer.write(LyJsonUtil.toJson(result));
            writer.flush();
        } catch (IOException e) {
            logger.error("write response stream exception", e);
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
