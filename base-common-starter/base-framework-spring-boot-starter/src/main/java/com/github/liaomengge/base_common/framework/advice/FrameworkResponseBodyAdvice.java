package com.github.liaomengge.base_common.framework.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.liaomengge.base_common.framework.advice.annotation.IgnoreResponseAdvice;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.json.LyJacksonUtil;
import com.github.liaomengge.service.base_framework.base.DataResult;
import com.github.liaomengge.service.base_framework.base.code.IResultCode;
import com.github.liaomengge.service.base_framework.base.code.SystemResultCode;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by liaomengge on 2020/11/23.
 * 针对controller返回void，如果请求参数含HttpServletResponse，则不会走该包装内，具体可见：http://riun.xyz/work/101
 */
@Order
@AllArgsConstructor
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class FrameworkResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final String STRING_MESSAGE_CONVERT = "org.springframework.http.converter.StringHttpMessageConverter";

    private boolean enabled;
    private final String[] ignorePackages;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (!enabled) {
            return false;
        }
        Class<?> declaringClass = returnType.getDeclaringClass();
        boolean isClassIgnoreResponseAdvice = declaringClass.isAnnotationPresent(IgnoreResponseAdvice.class);
        boolean isMethodIgnoreResponseAdvice = returnType.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class);
        if (isClassIgnoreResponseAdvice || isMethodIgnoreResponseAdvice || DataResult.class.equals(returnType.getGenericParameterType())) {
            return false;
        }
        if (Objects.nonNull(ignorePackages)) {
            return Arrays.stream(ignorePackages).noneMatch(val -> ClassUtils.getPackageName(val).startsWith(val));
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (STRING_MESSAGE_CONVERT.equalsIgnoreCase(selectedConverterType.getName())) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataResult dataResult = DataResult.success(body);
            try {
                if (Objects.nonNull(objectMapper)) {
                    return objectMapper.writeValueAsString(dataResult);
                }
                return LyJacksonUtil.bean2Json(dataResult);
            } catch (Exception e) {
                return DataResult.fail(SystemResultCode.DATA_ERROR, e.getMessage(), LyThrowableUtil.getStackTrace(e));
            }
        }
        if (body instanceof DataResult) {
            return DataResult.success(body);
        }
        if (body instanceof IResultCode) {
            return DataResult.fail((IResultCode) body);
        }
        return DataResult.success(body);
    }
}
