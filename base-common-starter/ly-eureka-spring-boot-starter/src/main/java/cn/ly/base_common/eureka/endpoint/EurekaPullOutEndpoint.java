package cn.ly.base_common.eureka.endpoint;

import cn.ly.base_common.eureka.consts.EurekaConst;
import cn.ly.base_common.utils.error.LyThrowableUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;

import com.google.common.collect.Maps;
import com.netflix.appinfo.InstanceInfo;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by liaomengge on 2020/8/15.
 */
@Endpoint(id = EurekaConst.PULL_OUT_ENDPOINT)
public class EurekaPullOutEndpoint implements ApplicationContextAware {

    private static final Logger log = LyLogger.getInstance(EurekaPullOutEndpoint.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ReadOperation
    public Map<String, Object> pullOut() {
        Map<String, Object> retMap = Maps.newHashMap();
        try {
            EurekaRegistration eurekaRegistration = applicationContext.getBean(EurekaRegistration.class);
            eurekaRegistration.getApplicationInfoManager().setInstanceStatus(InstanceInfo.InstanceStatus.OUT_OF_SERVICE);

            InstanceInfo instanceInfo = eurekaRegistration.getApplicationInfoManager().getInfo();
            eurekaRegistration.getEurekaClient().setStatus(InstanceInfo.InstanceStatus.OUT_OF_SERVICE, instanceInfo);
            log.info("set service => {}, instance => {}, status => OUT_OF_SERVICE", eurekaRegistration.getServiceId(),
                    eurekaRegistration.getHost());
            retMap.put("status", instanceInfo.getStatus());
            retMap.put("success", true);
        } catch (Exception e) {
            retMap.put("exception", LyThrowableUtil.getStackTrace(e));
            retMap.put("success", false);
        }
        return retMap;
    }
}
