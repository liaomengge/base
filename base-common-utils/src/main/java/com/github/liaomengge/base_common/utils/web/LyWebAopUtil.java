package com.github.liaomengge.base_common.utils.web;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.InputStreamSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by liaomengge on 2019/12/8.
 */
@UtilityClass
public class LyWebAopUtil {

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
                return LyWebUtil.getRequestArrayParams(request);
            }
            if (args[0] instanceof WebRequest) {
                WebRequest request = (WebRequest) args[0];
                return request.getParameterMap();
            }
        }
        List<Object> parameterList = Lists.newArrayList();
        for (int i = 0; i < parameterAnnotations.length && i < args.length; i++) {
            if (matchAnnotation(parameterAnnotations[i])) {
                parameterList.add(args[i]);
            } else {
                parameterList.add(convertStreamArg(args[i]));
            }
        }
        return parameterList;
    }

    private boolean matchAnnotation(Annotation[] annotations) {
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
}
