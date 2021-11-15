package com.github.liaomengge.base_common.design.patterns.strategy.factory;

import com.github.liaomengge.base_common.utils.generic.LyGenericUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author liaomengge
 * @version GenericStrategyFactory.java, v 0.1 2021-11-05 10:22 liaomengge Exp $$
 */
public abstract class GenericStrategyFactory<K, V> implements ApplicationContextAware {

    protected final Map<K, V> strategyHandlerMap = Maps.newConcurrentMap();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext.getParent();
    }

    protected abstract K getStrategyTypeName(V value);

    @PostConstruct
    public void init() {
        Class<V> clazz = LyGenericUtil.getActualTypeArguments4GenericInterface(this.getClass(), 1);
        Map<String, V> tMap = this.applicationContext.getBeansOfType(clazz);
        tMap.forEach((key, value) -> strategyHandlerMap.put(this.getStrategyTypeName(value), value));
    }
}
