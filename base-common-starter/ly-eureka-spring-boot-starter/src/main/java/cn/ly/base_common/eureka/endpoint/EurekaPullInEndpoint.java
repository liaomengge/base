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
@Endpoint(id = EurekaConst.PULL_IN_ENDPOINT)
public class EurekaPullInEndpoint implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ReadOperation
    public InstanceInfo pullIn() {
        EurekaRegistration eurekaRegistration = applicationContext.getBean(EurekaRegistration.class);
        eurekaRegistration.getApplicationInfoManager().setInstanceStatus(InstanceInfo.InstanceStatus.UP);

        InstanceInfo instanceInfo = eurekaRegistration.getApplicationInfoManager().getInfo();
        eurekaRegistration.getEurekaClient().setStatus(InstanceInfo.InstanceStatus.UP, instanceInfo);
        log.info("set service => {}, instance => {}, status => UP", eurekaRegistration.getServiceId(),
                eurekaRegistration.getHost());

        return instanceInfo;
    }
}
