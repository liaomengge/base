package com.github.liaomengge.base_common.support.loader;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;

/**
 * Created by liaomengge on 2020/11/10.
 */
public class ExtReflectionLoader<T> {

    private volatile boolean init = false;

    private Class<T> serviceType;
    private Map<Class<T>, Reflections> reflectionsMap;

    @Getter
    private Map<String, Class<? extends T>> classMap = Maps.newConcurrentMap();
    @Getter
    private Map<String, T> instanceMap = Maps.newConcurrentMap();

    private ExtReflectionLoader(Class<T> serviceType) {
        this.serviceType = serviceType;
        this.reflectionsMap = Maps.newConcurrentMap();
        this.checkInit();
    }

    private void checkInit() {
        if (!init) {
            this.loadReflections();
        }
    }

    private synchronized void loadReflections() {
        if (init) {
            return;
        }
        Reflections reflections = reflectionsMap.computeIfAbsent(serviceType,
                val -> new Reflections(val.getPackage().getName()));
        Set<Class<? extends T>> classSet = reflections.getSubTypesOf(this.serviceType);
        for (Class<? extends T> clazz : classSet) {
            T t;
            try {
                t = clazz.newInstance();
                instanceMap.putIfAbsent(clazz.getName(), t);
            } catch (Exception e) {
                throw new RuntimeException(clazz.getName() + ": Error when getExtension ", e);
            }
            classMap.putIfAbsent(clazz.getName(), clazz);
        }
        this.init = true;
    }

    public static <T> ExtReflectionLoader getLoader(Class<T> serviceType) {
        return new ExtReflectionLoader<>(serviceType);
    }
}
