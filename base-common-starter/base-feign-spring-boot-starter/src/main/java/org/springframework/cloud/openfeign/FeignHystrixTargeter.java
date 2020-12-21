package org.springframework.cloud.openfeign;

import com.github.liaomengge.base_common.feign.fallback.FeignFallbackFactory;
import feign.Feign;
import feign.Target;
import feign.hystrix.FallbackFactory;
import feign.hystrix.HystrixFeign;
import feign.hystrix.SetterFactory;
import org.springframework.util.StringUtils;

/**
 * Created by liaomengge on 2020/12/21.
 */
public class FeignHystrixTargeter implements Targeter {

    @Override
    public <T> T target(FeignClientFactoryBean factory, Feign.Builder feign,
                        FeignContext context, Target.HardCodedTarget<T> target) {
        if (!(feign instanceof feign.hystrix.HystrixFeign.Builder)) {
            return feign.target(target);
        }
        feign.hystrix.HystrixFeign.Builder builder = (feign.hystrix.HystrixFeign.Builder) feign;
        String name = StringUtils.isEmpty(factory.getContextId()) ? factory.getName() : factory.getContextId();
        SetterFactory setterFactory = getOptional(name, context, SetterFactory.class);
        if (setterFactory != null) {
            builder.setterFactory(setterFactory);
        }
        Class<?> fallback = factory.getFallback();
        if (fallback != void.class) {
            return targetWithFallback(name, context, target, builder, fallback);
        }
        Class<?> fallbackFactory = factory.getFallbackFactory();
        if (fallbackFactory != void.class) {
            return targetWithFallbackFactory(name, context, target, builder, fallbackFactory);
        }

        return builder.target(target, new FeignFallbackFactory<>(target));
    }

    private <T> T targetWithFallbackFactory(String feignClientName, FeignContext context,
                                            Target.HardCodedTarget<T> target, HystrixFeign.Builder builder,
                                            Class<?> fallbackFactoryClass) {
        FallbackFactory<? extends T> fallbackFactory = (FallbackFactory<? extends T>) getFromContext(
                "fallbackFactory", feignClientName, context, fallbackFactoryClass, FallbackFactory.class);
        return builder.target(target, fallbackFactory);
    }

    private <T> T targetWithFallback(String feignClientName, FeignContext context,
                                     Target.HardCodedTarget<T> target, HystrixFeign.Builder builder,
                                     Class<?> fallback) {
        T fallbackInstance = getFromContext("fallback", feignClientName, context, fallback, target.type());
        return builder.target(target, fallbackInstance);
    }

    private <T> T getFromContext(String fallbackMechanism, String feignClientName,
                                 FeignContext context, Class<?> beanType, Class<T> targetType) {
        Object fallbackInstance = context.getInstance(feignClientName, beanType);
        if (fallbackInstance == null) {
            throw new IllegalStateException(String.format(
                    "No " + fallbackMechanism
                            + " instance of type %s found for feign client %s",
                    beanType, feignClientName));
        }

        if (!targetType.isAssignableFrom(beanType)) {
            throw new IllegalStateException(String.format("Incompatible "
                            + fallbackMechanism
                            + " instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign " +
                            "client %s",
                    beanType, targetType, feignClientName));
        }
        return (T) fallbackInstance;
    }

    private <T> T getOptional(String feignClientName, FeignContext context,
                              Class<T> beanType) {
        return context.getInstance(feignClientName, beanType);
    }
}
