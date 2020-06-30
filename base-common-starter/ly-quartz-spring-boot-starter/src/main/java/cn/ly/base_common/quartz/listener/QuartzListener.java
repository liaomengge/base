package cn.ly.base_common.quartz.listener;

import cn.ly.base_common.quartz.QuartzProperties;
import cn.ly.base_common.quartz.listener.util.TriggerUtil;
import cn.ly.base_common.utils.date.MwJdk8DateUtil;
import cn.ly.base_common.utils.json.MwJsonUtil;
import cn.ly.base_common.utils.log4j2.MwLogger;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.PropertySourcesPropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2019/7/18.
 */
public class QuartzListener implements EnvironmentAware, ApplicationListener<EnvironmentChangeEvent>, Ordered {

    private static final Logger logger = MwLogger.getInstance(QuartzListener.class);

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
        Optional<String> quartzChange = event.getKeys().stream().filter(val -> StringUtils.startsWithIgnoreCase(val,
                "ly.quartz")).findFirst();
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
                                logger.warn("Trigger[{}], old core expression[{}] or new core expression[{}] is not " +
                                        "Illegal");
                                continue;
                            }
                            if (!StringUtils.equals(oldCronExpression, newCronExpression)) {
                                logger.info("Trigger[{}], old core expression[{}], new core expression[{}], change " +
                                                "time ===> {}",
                                        name, oldCronExpression, newCronExpression, MwJdk8DateUtil.getNowDate2String());
                                CronTriggerImpl newTrigger = TriggerUtil.clone(trigger);
                                newTrigger.setCronExpression(newCronExpression);
                                scheduler.rescheduleJob(triggerKey, newTrigger);
                            }
                        } catch (Exception e) {
                            logger.error("ReScheduleJob失败", e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("解析JobInfo失败", e);
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
        Map<String, LinkedHashMap<String, Object>> jobPropertiesMap = Maps.newHashMap();
        MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
        new RelaxedDataBinder(jobPropertiesMap, "ly.quartz").bind(new PropertySourcesPropertyValues(propertySources));
        LinkedHashMap<String, Object> subJobMap = jobPropertiesMap.get("jobs");
        subJobMap = subJobMap.entrySet().stream()
                .filter(val -> val.getValue() instanceof LinkedHashMap && StringUtils.isNumeric(val.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal,
                        LinkedHashMap::new));
        String jobJson = MwJsonUtil.toJson(subJobMap);
        Map<String, QuartzProperties.JobInfo> jobJsonMap = MwJsonUtil.fromJson(jobJson,
                new TypeReference<Map<String, QuartzProperties.JobInfo>>() {
                });
        Optional.ofNullable(jobJsonMap).ifPresent(val -> {
            val.values().forEach(jobInfo -> {
                String pkgClassName = this.quartzProperties.getPackageName(jobInfo);
                triggerDetailMap.put(pkgClassName + "Trigger", jobInfo.getCronExpression());
            });
        });
        return triggerDetailMap;
    }
}
