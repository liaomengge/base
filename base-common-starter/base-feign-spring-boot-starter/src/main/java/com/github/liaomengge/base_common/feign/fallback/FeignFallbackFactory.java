package com.github.liaomengge.base_common.feign.fallback;

import com.github.liaomengge.base_common.feign.fallback.annotation.FallbackReturn;
import com.github.liaomengge.base_common.feign.fallback.handler.FallbackReturnHandler;
import com.github.liaomengge.base_common.support.proxy.CglibDynamicProxyFactory;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.github.liaomengge.base_common.utils.reflect.LyClassUtil;
import feign.Feign;
import feign.Target;
import feign.hystrix.FallbackFactory;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

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
        return CglibDynamicProxyFactory.getProxy(target.type(), new FeignFallbackInterceptor<>(target, cause));
    }

    @AllArgsConstructor
    protected static class FeignFallbackInterceptor<T> implements MethodInterceptor {

        private static final Logger log = LyLogger.getInstance(FeignFallbackInterceptor.class);

        private final Target<T> target;
        private final Throwable cause;

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            log.error("call service[" + target.name() + "], " +
                    "feign key[" + Feign.configKey(target.type(), method) + "] fallback...", cause);
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
            return Objects.equals(target, that.target) &&
                    Objects.equals(cause, that.cause);
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
