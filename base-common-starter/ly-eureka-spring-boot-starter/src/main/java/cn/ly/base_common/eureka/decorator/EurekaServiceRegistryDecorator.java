package cn.ly.base_common.eureka.decorator;

import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * Created by liaomengge on 2020/8/15.
 */
public class EurekaServiceRegistryDecorator extends EurekaServiceRegistry {

    private final ConfigurableEnvironment environment;
    private final EurekaServiceRegistry eurekaServiceRegistry;

    public EurekaServiceRegistryDecorator(ConfigurableEnvironment environment,
                                          EurekaServiceRegistry eurekaServiceRegistry) {
        this.environment = environment;
        this.eurekaServiceRegistry = eurekaServiceRegistry;
    }

    @Override
    public void register(EurekaRegistration reg) {
        if (isRegisterEnabled(environment)) {
            eurekaServiceRegistry.register(reg);
        }
    }

    @Override
    public void deregister(EurekaRegistration reg) {
        if (isRegisterEnabled(environment)) {
            eurekaServiceRegistry.deregister(reg);
        }
    }

    @Override
    public void setStatus(EurekaRegistration registration, String status) {
        if (isRegisterEnabled(environment)) {
            eurekaServiceRegistry.setStatus(registration, status);
        }
    }

    @Override
    public Object getStatus(EurekaRegistration registration) {
        return eurekaServiceRegistry.getStatus(registration);
    }

    @Override
    public void close() {
        eurekaServiceRegistry.close();
    }

    private Boolean isRegisterEnabled(Environment environment) {
        return environment.getProperty("ly.eureka.registry.enabled", Boolean.class, Boolean.TRUE);
    }

}
