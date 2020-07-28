package cn.ly.base_common.influx.aspect;

import cn.ly.base_common.influx.helper.InfluxHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liaomengge on 2020/7/21.
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class InfluxElapsedTimeAspect {

    private final InfluxHelper influxHelper;

    @Pointcut("@annotation(cn.ly.base_common.influx.aspect.EnableElapsedTime)")
    public void annotationPointcut() {
    }

    @Around("annotationPointcut() && @annotation(enableElapsedTime)")
    public Object doAround(ProceedingJoinPoint joinPoint, EnableElapsedTime enableElapsedTime) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            try {
                influxHelper.write(enableElapsedTime.value(), buildMethodFields(methodName, (endTime - startTime)));
            } catch (Exception e) {
                log.warn("log method elapsed fail", e);
            }
        }
    }

    private Map<String, Object> buildMethodFields(String methodName, long elapsedTime) {
        Map<String, Object> methodFieldMap = new HashMap<>();
        methodFieldMap.put("methodName", methodName);
        methodFieldMap.put("elapsedTime", elapsedTime);
        //兼容老版本
        methodFieldMap.put("costMs", elapsedTime);
        return methodFieldMap;
    }
}
