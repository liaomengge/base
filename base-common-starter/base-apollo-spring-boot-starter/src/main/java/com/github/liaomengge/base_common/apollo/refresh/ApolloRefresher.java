package com.github.liaomengge.base_common.apollo.refresh;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.github.liaomengge.base_common.apollo.ApolloProperties;
import com.github.liaomengge.base_common.apollo.consts.ApolloConst;
import com.github.liaomengge.base_common.apollo.enums.RefreshTypeEnum;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Optional;
import java.util.Set;

/**
 * Created by liaomengge on 2020/8/1.
 */
public class ApolloRefresher implements ApplicationContextAware {

    private static final Logger log = LyLogger.getInstance(ApolloRefresher.class);

    private ApplicationContext applicationContext;

    private final RefreshScope refreshScope;
    private final ApolloProperties apolloProperties;

    public ApolloRefresher(RefreshScope refreshScope, ApolloProperties apolloProperties) {
        this.refreshScope = refreshScope;
        this.apolloProperties = apolloProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void refresh(ConfigChangeEvent changeEvent) {
        RefreshTypeEnum refreshTypeEnum = getRefreshTypeEnum(changeEvent);
        Set<String> changeKeys = this.apolloProperties.getRefreshScope().getChangeKeys();
        switch (refreshTypeEnum) {
            case SCOPE:
                refreshScope(changeKeys);
                return;
            case ALL:
                refreshProperties(changeEvent);
                refreshScope(changeKeys);
                return;
            case PROPERTIES:
            default:
                refreshProperties(changeEvent);
                return;
        }
    }

    private RefreshTypeEnum getRefreshTypeEnum(ConfigChangeEvent changeEvent) {
        Set<String> changeKeys = changeEvent.changedKeys();
        if (CollectionUtils.containsAny(changeKeys, ApolloConst.REFRESH_TYPE_KEY)) {
            ConfigChange configChange = changeEvent.getChange(ApolloConst.REFRESH_TYPE_KEY);
            return Optional.ofNullable(configChange).map(ConfigChange::getNewValue)
                    .map(RefreshTypeEnum::getRefreshTypeEnum).orElse(RefreshTypeEnum.PROPERTIES);
        }

        return RefreshTypeEnum.getRefreshTypeEnum(applicationContext.getEnvironment().getProperty(ApolloConst.REFRESH_TYPE_KEY));
    }

    private void refreshProperties(ConfigChangeEvent changeEvent) {
        try {
            applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
        } catch (Exception e) {
            log.warn("refresh properties fail", e);
        }
    }

    private void refreshScope(Set<String> changeKeys) {
        try {
            if (CollectionUtils.isEmpty(changeKeys)) {
                refreshScope.refreshAll();
                return;
            }
            changeKeys.stream().forEach(refreshScope::refresh);
        } catch (Exception e) {
            log.warn("refresh scope fail", e);
        }
    }
}
