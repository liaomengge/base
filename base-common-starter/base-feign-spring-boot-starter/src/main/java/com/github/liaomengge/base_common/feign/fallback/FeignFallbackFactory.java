package com.github.liaomengge.base_common.feign.fallback;

import com.github.liaomengge.base_common.feign.fallback.annotation.FallbackReturn;
import com.github.liaomengge.base_common.feign.fallback.handler.FallbackReturnHandler;
import com.github.liaomengge.base_common.support.logger.JsonLogger;
import com.github.liaomengge.base_common.utils.reflect.LyClassUtil;
import feign.Feign;
import feign.Target;
import feign.hystrix.FallbackFactory;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by liaomengge on 2020/12/21.
 */
@AllArgsConstructor
public class FeignFallbackFactory<T> implements FallbackFactory<T> {

    private final Target<T> target;

    @Override
    public T create(Throwable cause) {
        return ProxyFactory.getProxy(target.type(), new FeignFallbackInterceptor<>(target, cause));
    }

    @AllArgsConstructor
    protected static class FeignFallbackInterceptor<T> implements MethodInterceptor {

        private static final JsonLogger log = JsonLogger.getInstance(FeignFallbackInterceptor.class);

        private final Target<T> target;
        private final Throwable cause;

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            Method method = methodInvocation.getMethod();
            if (ReflectionUtils.isObjectMethod(method)) {
                return null;
            }
            log.error("call service[" + target.name() + "], " + "feign config key[" + Feign.configKey(target.type(),
                    method) + "] fallback...", cause);
            FallbackReturn fallbackReturn = LyClassUtil.getAnnotation(method, FallbackReturn.class);
            if (Objects.nonNull(fallbackReturn)) {
                FallbackReturnHandler handler;
                try {
                    Class<? extends FallbackReturnHandler> handlerClass = fallbackReturn.handler();
                    handler = BeanUtils.instantiateClass(handlerClass);
                    return handler.apply(target, method, cause);
                } catch (Exception e) {
                    log.error("fallback handler fail", e);
                }
            }
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FeignFallbackInterceptor<?> that = (FeignFallbackInterceptor<?>) o;
            return target.equals(that.target) && cause.equals(that.cause);
        }

        @Override
        public int hashCode() {
            return Objects.hash(target, cause);
        }

        @Override
        public String toString() {
            return "FeignFallbackInterceptor{" +
                    "target=" + target +
                    ", cause=" + cause +
                    '}';
        }
    }
}
