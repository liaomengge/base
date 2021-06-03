package com.github.liaomengge.base_common.strategy.factory;

import com.github.liaomengge.base_common.strategy.annotation.EventStrategyType;
import com.github.liaomengge.base_common.strategy.handler.EventStrategyHandler;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;

/**
 * Created by liaomengge on 2021/6/3.
 */
public class EventStrategyFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public static Map<String, Class<EventStrategyHandler>> eventHandlerClassMap = Maps.newConcurrentMap();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        Map<String, Object> eventStrategyTypeMap = applicationContext.getBeansWithAnnotation(EventStrategyType.class);
        for (Map.Entry<String, Object> entry : eventStrategyTypeMap.entrySet()) {
            Object eventStrategyHandler = entry.getValue();
            if (Objects.isNull(eventStrategyHandler)) {
                continue;
            }
            Class<?> handlerClass = eventStrategyHandler.getClass();
            EventStrategyType eventStrategyType = AnnotationUtils.findAnnotation(handlerClass, EventStrategyType.class);
            if (Objects.isNull(eventStrategyType)) {
                continue;
            }
            eventHandlerClassMap.put(getEventStrategyTypeName(eventStrategyType),
                    (Class<EventStrategyHandler>) handlerClass);
        }
    }

    public EventStrategyHandler getHandler(EventStrategyType eventStrategyType) {
        Class<EventStrategyHandler> handlerClass =
                eventHandlerClassMap.get(getEventStrategyTypeName(eventStrategyType));
        if (Objects.isNull(handlerClass)) {
            return null;
        }
        return applicationContext.getBean(handlerClass);
    }

    public EventStrategyHandler getHandler(String value, String category) {
        Class<EventStrategyHandler> handlerClass = eventHandlerClassMap.get(category + '#' + value);
        if (Objects.isNull(handlerClass)) {
            return null;
        }
        return applicationContext.getBean(handlerClass);
    }

    private String getEventStrategyTypeName(EventStrategyType eventStrategyType) {
        return eventStrategyType.category() + '#' + eventStrategyType.value();
    }
}
