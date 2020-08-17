package cn.ly.base_common.eureka.endpoint;

import cn.ly.base_common.eureka.consts.EurekaConst;
import com.netflix.appinfo.InstanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by liaomengge on 2020/8/15.
 */
@Slf4j
@Endpoint(id = EurekaConst.PULL_OUT_ENDPOINT)
public class EurekaPullOutEndpoint implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ReadOperation
    public InstanceInfo pullOut() {
        EurekaRegistration eurekaRegistration = applicationContext.getBean(EurekaRegistration.class);
        eurekaRegistration.getApplicationInfoManager().setInstanceStatus(InstanceInfo.InstanceStatus.OUT_OF_SERVICE);

        InstanceInfo instanceInfo = eurekaRegistration.getApplicationInfoManager().getInfo();
        eurekaRegistration.getEurekaClient().setStatus(InstanceInfo.InstanceStatus.OUT_OF_SERVICE, instanceInfo);
        log.info("set service => {}, instance => {}, status => OUT_OF_SERVICE", eurekaRegistration.getServiceId(),
                eurekaRegistration.getHost());

        return instanceInfo;
    }
}
