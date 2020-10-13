package com.github.liaomengge.base_common.quartz.listener.util;

import org.quartz.impl.triggers.CronTriggerImpl;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2019/7/19.
 */
@UtilityClass
public class TriggerUtil {

    public CronTriggerImpl clone(CronTriggerImpl cronTrigger) {
        CronTriggerImpl newCoreTrigger = (CronTriggerImpl) cronTrigger.clone();
        newCoreTrigger.setPreviousFireTime(null);
        newCoreTrigger.setNextFireTime(null);
        return newCoreTrigger;
    }
}
