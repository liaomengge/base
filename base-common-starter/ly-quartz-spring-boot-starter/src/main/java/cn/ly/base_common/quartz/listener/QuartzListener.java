package cn.ly.base_common.quartz.listener;

import cn.ly.base_common.quartz.QuartzProperties;
import cn.ly.base_common.quartz.listener.util.TriggerUtil;
import cn.ly.base_common.utils.binder.LyBinderUtil;
import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Created by liaomengge on 2019/7/18.
 */
public class QuartzListener implements EnvironmentAware, ApplicationListener<EnvironmentChangeEvent>, Ordered {

    private static final Logger log = LyLogger.getInstance(QuartzListener.class);

    private Environment environment;

    @Autowired
    private QuartzProperties quartzProperties;

    @Autowired(required = false)
    private SchedulerFactoryBean schedulerFactoryBean;

    private final List<TriggerKey> triggerKeys;

    public QuartzListener(List<TriggerKey> triggerKeys) {
        this.triggerKeys = triggerKeys;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Optional<String> quartzChange = event.getKeys().stream()
                .filter(val -> StringUtils.startsWithIgnoreCase(val, "ly.quartz")).findFirst();
        quartzChange.ifPresent(val -> Optional.ofNullable(schedulerFactoryBean).ifPresent(val2 -> {
            try {
                Map<String, String> triggerDetailMap = buildTriggerDetail();
                Scheduler scheduler = val2.getScheduler();
                if (Objects.nonNull(triggerKeys) && Objects.nonNull(scheduler) && !scheduler.isShutdown()) {
                    for (TriggerKey triggerKey : triggerKeys) {
                        try {
                            CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
                            String name = trigger.getKey().getName();
                            String oldCronExpression = trigger.getCronExpression();
                            String newCronExpression = triggerDetailMap.get(name);
                            if (StringUtils.isBlank(oldCronExpression) || StringUtils.isBlank(newCronExpression)) {
                                log.warn("Trigger[{}], old core expression[{}] or new core expression[{}] is not " +
                                        "Illegal");
                                continue;
                            }
                            if (!StringUtils.equals(oldCronExpression, newCronExpression)) {
                                log.info("Trigger[{}], old core expression[{}], new core expression[{}], change " +
                                                "time ===> {}",
                                        name, oldCronExpression, newCronExpression, LyJdk8DateUtil.getNowDate2String());
                                CronTriggerImpl newTrigger = TriggerUtil.clone(trigger);
                                newTrigger.setCronExpression(newCronExpression);
                                scheduler.rescheduleJob(triggerKey, newTrigger);
                            }
                        } catch (Exception e) {
                            log.error("ReScheduleJob失败", e);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("解析JobInfo失败", e);
            }
        }));
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    /**
     * env获取最新的值
     *
     * @return
     */
    private Map<String, String> buildTriggerDetail() {
        Map<String, String> triggerDetailMap = Maps.newHashMap();
        QuartzProperties quartzProperties = LyBinderUtil.bind((ConfigurableEnvironment) environment, "ly.quartz",
                QuartzProperties.class);
        Optional.ofNullable(quartzProperties).map(QuartzProperties::getJobs).ifPresent(jobInfoList -> {
            jobInfoList.forEach(jobInfo -> {
                String pkgClassName = this.quartzProperties.getPackageName(jobInfo);
                triggerDetailMap.put(pkgClassName + "Trigger", jobInfo.getCronExpression());
            });
        });
        return triggerDetailMap;
    }
}
