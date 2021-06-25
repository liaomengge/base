package com.github.liaomengge.base_common.apollo.listener;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.github.liaomengge.base_common.apollo.refresh.ApolloRefresher;
import com.github.liaomengge.base_common.apollo.refresh.conditional.ApolloConditionalRefresh;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by liaomengge on 2020/8/1.
 */
@Slf4j
public class ApolloRefreshListener implements ConfigChangeListener {
    
    private AtomicBoolean isReady = new AtomicBoolean(false);
    private AtomicBoolean isRefresh = new AtomicBoolean(false);

    private final ApolloRefresher apolloRefresher;
    private final ApolloConditionalRefresh apolloConditionalRefresh;

    public ApolloRefreshListener(ApolloRefresher apolloRefresher, ApolloConditionalRefresh apolloConditionalRefresh) {
        this.apolloRefresher = apolloRefresher;
        this.apolloConditionalRefresh = apolloConditionalRefresh;
    }

    @EventListener
    public void onRefresh(ApplicationReadyEvent readyEvent) {
        isReady.compareAndSet(false, true);
        log.info("apollo refresh listener ready!");
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        if (isReady.get() && !isRefresh.get()) {
            isRefresh.compareAndSet(false, true);
            log.info("start refresh on change key => {}", changeEvent.changedKeys().toString());
            apolloRefresher.refresh(changeEvent);
            Optional.ofNullable(apolloConditionalRefresh).ifPresent(val -> val.refresh(changeEvent));
            log.info("end refresh on change key => {}", changeEvent.changedKeys().toString());
            isRefresh.compareAndSet(true, false);
        }
    }
}
