package com.github.liaomengge.base_common.support.loader;

import com.github.liaomengge.base_common.utils.collection.LyMapUtil;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.reflections.Reflections;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by liaomengge on 2020/11/10.
 */
public class ExtReflectionLoader<T> {

    private Class<T> serviceType;
    private Map<Class<T>, Reflections> reflectionsMap = Maps.newConcurrentMap();

    private AtomicBoolean initialized = new AtomicBoolean(false);

    @Getter
    private Map<String, Class<? extends T>> classMap = Maps.newConcurrentMap();
    @Getter
    private Map<String, T> instanceMap = Maps.newConcurrentMap();

    private ExtReflectionLoader(Class<T> serviceType) {
        this.serviceType = serviceType;
        this.loadReflections();
    }

    private void loadReflections() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }
        Reflections reflections = LyMapUtil.computeIfAbsent(reflectionsMap, serviceType,
                val -> new Reflections(ClassUtils.getPackageName(val)));
        Set<Class<? extends T>> classSet = reflections.getSubTypesOf(this.serviceType);
        for (Class<? extends T> clazz : classSet) {
            T t;
            try {
                t = BeanUtils.instantiateClass(clazz);
                instanceMap.putIfAbsent(clazz.getName(), t);
            } catch (Exception e) {
                throw new RuntimeException(clazz.getName() + ": Error when getLoader ", e);
            }
            classMap.putIfAbsent(clazz.getName(), clazz);
        }
    }

    public static <T> ExtReflectionLoader getLoader(Class<T> serviceType) {
        return new ExtReflectionLoader<>(serviceType);
    }
}
