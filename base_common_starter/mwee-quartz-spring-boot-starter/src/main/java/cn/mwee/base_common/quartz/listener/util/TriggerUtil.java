package cn.mwee.base_common.quartz.listener.util;

import lombok.experimental.UtilityClass;
import org.quartz.impl.triggers.CronTriggerImpl;

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
