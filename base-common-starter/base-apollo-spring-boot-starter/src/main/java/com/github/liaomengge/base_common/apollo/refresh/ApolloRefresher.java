package com.github.liaomengge.base_common.apollo.refresh;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * Created by liaomengge on 2020/8/1.
 */
public class ApolloRefresher implements ApplicationContextAware {

    private static final Logger log = LyLogger.getInstance(ApolloRefresher.class);

    private final String REFRESH_TYPE_KEY = "base.apollo.refresh-type";

    private ApplicationContext applicationContext;

    private final RefreshScope refreshScope;

    public ApolloRefresher(RefreshScope refreshScope) {
        this.refreshScope = refreshScope;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void refresh(ConfigChangeEvent changeEvent) {
        RefreshType refreshType = getRefreshType(changeEvent);
        switch (refreshType) {
            case SCOPE:
                refreshScope();
                return;
            case ALL:
                refreshProperties(changeEvent);
                refreshScope();
                return;
            case PROPERTIES:
            default:
                refreshProperties(changeEvent);
                return;
        }
    }

    private RefreshType getRefreshType(ConfigChangeEvent changeEvent) {
        Set<String> changeKeys = changeEvent.changedKeys();
        if (CollectionUtils.containsAny(changeKeys, REFRESH_TYPE_KEY)) {
            ConfigChange configChange = changeEvent.getChange(REFRESH_TYPE_KEY);
            return Optional.ofNullable(configChange).map(ConfigChange::getNewValue)
                    .map(RefreshType::getRefreshType).orElse(RefreshType.PROPERTIES);
        }

        return RefreshType.getRefreshType(applicationContext.getEnvironment().getProperty(REFRESH_TYPE_KEY));
    }

    private void refreshProperties(ConfigChangeEvent changeEvent) {
        try {
            applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
        } catch (Exception e) {
            log.warn("refresh properties fail", e);
        }
    }

    private void refreshScope() {
        try {
            refreshScope.refreshAll();
        } catch (Exception e) {
            log.warn("refresh scope fail", e);
        }
    }

    public enum RefreshType {
        PROPERTIES,
        SCOPE,
        ALL;

        public static RefreshType getRefreshType(String name) {
            return Arrays.stream(values())
                    .filter(val -> StringUtils.equalsIgnoreCase(val.name(), name))
                    .findFirst().orElse(RefreshType.PROPERTIES);
        }
    }
}
