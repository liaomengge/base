package com.github.liaomengge.base_common.utils.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.experimental.UtilityClass;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;

import java.util.function.Supplier;

/**
 * Created by liaomengge on 2020/12/18.
 */
@UtilityClass
public class ThreadLocalUtil {

    public <T> ThreadLocal<T> getThreadLocal() {
        return new ThreadLocal<>();
    }

    public <T> ThreadLocal<T> getThreadLocal(Supplier<? extends T> supplier) {
        return ThreadLocal.withInitial(supplier);
    }

    public <T> InheritableThreadLocal<T> getInheritableThreadLocal() {
        return new InheritableThreadLocal<>();
    }

    public <T> NamedThreadLocal<T> getNamedThreadLocal(String name) {
        return new NamedThreadLocal<>(name);
    }

    public <T> NamedInheritableThreadLocal<T> getNamedInheritableThreadLocal(String name) {
        return new NamedInheritableThreadLocal<>(name);
    }

    public <T> TransmittableThreadLocal<T> getTransmittableThreadLocal() {
        return new TransmittableThreadLocal<>();
    }
}
