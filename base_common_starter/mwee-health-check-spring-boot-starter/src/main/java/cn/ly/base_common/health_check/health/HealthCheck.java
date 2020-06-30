package cn.ly.base_common.health_check.health;

import cn.ly.base_common.health_check.health.domain.HealthInfo;
import cn.ly.base_common.health_check.health.domain.HealthInfo.Status;
import cn.mwee.base_common.utils.error.MwThrowableUtil;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by liaomengge on 2019/7/11.
 */
public abstract class HealthCheck implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public final HealthInfo health() {
        HealthInfo healthInfo = new HealthInfo();
        try {
            doHealthCheck(healthInfo);
        } catch (Exception e) {
            healthInfo.setStatus(Status.DOWN)
                    .setDetails(ImmutableMap.of("Exception", MwThrowableUtil.getStackTrace(e)));
        }
        return healthInfo;
    }

    protected abstract void doHealthCheck(HealthInfo healthInfo) throws Exception;
}
