package com.github.liaomengge.base_common.utils.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.experimental.UtilityClass;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Created by liaomengge on 2020/12/18.
 */
@UtilityClass
public class LyThreadLocalUtil {

    public <T> ThreadLocal<T> getThreadLocal() {
        return new ThreadLocal<>();
    }

    public <T> ThreadLocal<T> getThreadLocal(Supplier<? extends T> supplier) {
        return ThreadLocal.withInitial(supplier);
    }

    public <T> NamedThreadLocal<T> getNamedThreadLocal(String name) {
        return new NamedThreadLocal<>(name);
    }

    public <T> NamedThreadLocal<T> getNamedThreadLocal(String name, Supplier<? extends T> supplier) {
        return new SuppliedNamedThreadLocal<>(name, supplier);
    }

    public <T> InheritableThreadLocal<T> getInheritableThreadLocal() {
        return new InheritableThreadLocal<>();
    }

    public <T> NamedInheritableThreadLocal<T> getNamedInheritableThreadLocal(String name) {
        return new NamedInheritableThreadLocal<>(name);
    }

    public <T> NamedInheritableThreadLocal<T> getNamedInheritableThreadLocal(String name,
                                                                             Supplier<? extends T> supplier) {
        return new SuppliedNamedInheritableThreadLocal<>(name, supplier);
    }

    public <T> TransmittableThreadLocal<T> getTransmittableThreadLocal() {
        return new TransmittableThreadLocal<>();
    }

    public <T> TransmittableThreadLocal<T> getNamedTransmittableThreadLocal(String name) {
        return new NamedTransmittableThreadLocal<>(name);
    }

    public <T> TransmittableThreadLocal<T> getNamedTransmittableThreadLocal(String name,
                                                                            Supplier<? extends T> supplier) {
        return new SuppliedNamedTransmittableThreadLocal<>(name, supplier);
    }

    static final class SuppliedNamedThreadLocal<T> extends NamedThreadLocal<T> {

        private final Supplier<? extends T> supplier;

        public SuppliedNamedThreadLocal(String name, Supplier<? extends T> supplier) {
            super(name);
            this.supplier = Objects.requireNonNull(supplier);
        }

        @Override
        protected T initialValue() {
            return supplier.get();
        }
    }

    static final class SuppliedNamedInheritableThreadLocal<T> extends NamedInheritableThreadLocal<T> {

        private final Supplier<? extends T> supplier;

        public SuppliedNamedInheritableThreadLocal(String name, Supplier<? extends T> supplier) {
            super(name);
            this.supplier = Objects.requireNonNull(supplier);
        }

        @Override
        protected T initialValue() {
            return supplier.get();
        }
    }

    static final class NamedTransmittableThreadLocal<T> extends TransmittableThreadLocal<T> {

        private final String name;

        NamedTransmittableThreadLocal(String name) {
            Assert.hasText(name, "Name must not be empty");
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    static final class SuppliedNamedTransmittableThreadLocal<T> extends TransmittableThreadLocal<T> {

        private final String name;
        private final Supplier<? extends T> supplier;

        public SuppliedNamedTransmittableThreadLocal(String name, Supplier<? extends T> supplier) {
            Assert.hasText(name, "Name must not be empty");
            this.name = name;
            this.supplier = Objects.requireNonNull(supplier);
        }

        @Override
        protected T initialValue() {
            return supplier.get();
        }
    }
}
