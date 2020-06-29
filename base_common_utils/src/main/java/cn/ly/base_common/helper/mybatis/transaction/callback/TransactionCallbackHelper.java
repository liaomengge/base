package cn.ly.base_common.helper.mybatis.transaction.callback;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Created by liaomengge on 2019/12/20.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransactionCallbackHelper {

    @Around("@annotation(cn.ly.base_common.helper.mybatis.transaction.callback.TransactionCallBack)")
    public Object methodAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object obj = joinPoint.proceed();
            TransactionCallbackManager.invokeSuccess();
            return obj;
        } catch (Throwable t) {
            TransactionCallbackManager.invokeOnThrowable(t);
            throw t;
        } finally {
            TransactionCallbackManager.clear();
        }
    }
}
