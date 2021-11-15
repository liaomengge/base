package com.github.liaomengge.base_common.utils.spel;

import com.github.liaomengge.base_common.utils.annotation.LyAnnotationUtil;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by liaomengge on 2021/7/17.
 */
@UtilityClass
public class LySpelUtil {

    private static SpelExpressionParser parser = new SpelExpressionParser();

    private static DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    public <T> T parse(String expr, Class<T> clazz) {
        return parse(expr, clazz, new StandardEvaluationContext());
    }

    public <T> T parse(String expr, Class<T> clazz, ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Map<String, Object> variableMap = buildVariable(joinPoint);
        context.setVariables(variableMap);
        return parse(expr, clazz, context);
    }

    public <T> T parseRootObject(String expr, Class<T> clazz, ProceedingJoinPoint joinPoint) {
        ContextRootObject contextRootObject = buildRootObject(joinPoint);
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(contextRootObject,
                contextRootObject.getMethod(), contextRootObject.getArgs(), discoverer);
        return parse(expr, clazz, context);
    }

    public <T> T parse(String expr, Class<T> clazz, EvaluationContext context) {
        Expression expression = parser.parseExpression(expr);
        return expression.getValue(context, clazz);
    }

    public <T> T parseTemplate(String expr, Class<T> clazz) {
        return parseTemplate(expr, clazz, new StandardEvaluationContext());
    }

    public <T> T parseTemplate(String expr, Class<T> clazz, ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Map<String, Object> variableMap = buildVariable(joinPoint);
        context.setVariables(variableMap);
        return parseTemplate(expr, clazz, context);
    }

    public <T> T parseRootObjectTemplate(String expr, Class<T> clazz, ProceedingJoinPoint joinPoint) {
        ContextRootObject contextRootObject = buildRootObject(joinPoint);
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(contextRootObject,
                contextRootObject.getMethod(), contextRootObject.getArgs(), discoverer);
        return parseTemplate(expr, clazz, context);
    }

    public <T> T parseTemplate(String expr, Class<T> clazz, EvaluationContext context) {
        return parseTemplate(expr, clazz, context, new TemplateParserContext());
    }

    public <T> T parseTemplate(String expr, Class<T> clazz, ParserContext parserContext) {
        return parseTemplate(expr, clazz, new StandardEvaluationContext(), parserContext);
    }

    public <T> T parseTemplate(String expr, Class<T> clazz, ParserContext parserContext,
                               ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Map<String, Object> variableMap = buildVariable(joinPoint);
        context.setVariables(variableMap);
        return parseTemplate(expr, clazz, context, parserContext);
    }

    public <T> T parseRootObjectTemplate(String expr, Class<T> clazz, ParserContext parserContext,
                                         ProceedingJoinPoint joinPoint) {
        ContextRootObject contextRootObject = buildRootObject(joinPoint);
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(contextRootObject,
                contextRootObject.getMethod(), contextRootObject.getArgs(), discoverer);
        return parseTemplate(expr, clazz, context, parserContext);
    }

    public <T> T parseTemplate(String expr, Class<T> clazz, EvaluationContext evaluationContext,
                               ParserContext parserContext) {
        Expression expression = parser.parseExpression(expr, parserContext);
        return expression.getValue(evaluationContext, clazz);
    }

    private Map<String, Object> buildVariable(ProceedingJoinPoint joinPoint) {
        Map<String, Object> retMap = Maps.newHashMap();
        Method method = LyAnnotationUtil.getSpecificMethod(joinPoint);
        String[] params = discoverer.getParameterNames(method);
        Object[] arguments = joinPoint.getArgs();
        if (ArrayUtils.isNotEmpty(params)) {
            for (int len = 0; len < params.length; len++) {
                retMap.put(params[len], arguments[len]);
            }
        }
        return retMap;
    }

    private ContextRootObject buildRootObject(ProceedingJoinPoint joinPoint) {
        Method method = LyAnnotationUtil.getSpecificMethod(joinPoint);
        Object[] args = joinPoint.getArgs();
        return ContextRootObject.builder().method(method).args(args).build();
    }

    @Data
    @Builder
    private class ContextRootObject {

        /**
         * 目标方法
         */
        private final Method method;

        /**
         * 方法参数
         */
        private final Object[] args;
    }
}
