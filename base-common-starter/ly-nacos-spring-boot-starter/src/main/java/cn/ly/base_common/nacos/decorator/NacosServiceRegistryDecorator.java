package cn.ly.base_common.nacos.decorator;

import cn.ly.base_common.nacos.NacosProperties;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.util.ReflectionUtils.rethrowRuntimeException;

/**
 * Created by liaomengge on 2020/8/17.
 */
@Slf4j
public class NacosServiceRegistryDecorator extends NacosServiceRegistry {

    private AtomicBoolean switchFlow = new AtomicBoolean(false);

    private final NacosProperties nacosProperties;
    private final ConfigurableEnvironment environment;
    private final NacosServiceRegistry nacosServiceRegistry;
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    private NamingService namingService;

    public NacosServiceRegistryDecorator(NacosDiscoveryProperties nacosDiscoveryProperties,
                                         NacosProperties nacosProperties, ConfigurableEnvironment environment,
                                         NacosServiceRegistry nacosServiceRegistry) {
        super(nacosDiscoveryProperties);
        this.nacosProperties = nacosProperties;
        this.environment = environment;
        this.nacosServiceRegistry = nacosServiceRegistry;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
        namingService = nacosDiscoveryProperties.namingServiceInstance();
    }

    @Override
    public void register(Registration registration) {
        if (isRegisterEnabled(environment)) {
            if (StringUtils.isEmpty(registration.getServiceId())) {
                log.warn("No service to register for nacos client...");
                return;
            }

            String serviceId = registration.getServiceId();
            String group = nacosDiscoveryProperties.getGroup();

            Instance instance = getNacosInstanceFromRegistration(registration);
            if (nacosProperties.getPull().isEnabled() && switchFlow.compareAndSet(false, true)) {
                instance.setEnabled(false);
                log.info("set init status[Disabled]...");
            }

            try {
                namingService.registerInstance(serviceId, group, instance);
                log.info("nacos registry, {} {} {}:{} register finished", group, serviceId,
                        instance.getIp(), instance.getPort());
            } catch (Exception e) {
                log.error("nacos registry, {} register failed...{},", serviceId,
                        registration.toString(), e);
                // rethrow a RuntimeException if the registration is failed.
                // issue : https://github.com/alibaba/spring-cloud-alibaba/issues/1132
                rethrowRuntimeException(e);
            }
        }
    }

    @Override
    public void deregister(Registration registration) {
        if (isRegisterEnabled(environment)) {
            nacosServiceRegistry.deregister(registration);
        }
    }

    @Override
    public void close() {
        if (isRegisterEnabled(environment)) {
            nacosServiceRegistry.close();
        }
    }

    @Override
    public void setStatus(Registration registration, String status) {
        if (isRegisterEnabled(environment)) {
            nacosServiceRegistry.setStatus(registration, status);
        }
    }

    @Override
    public Object getStatus(Registration registration) {
        return nacosServiceRegistry.getStatus(registration);
    }

    private Instance getNacosInstanceFromRegistration(Registration registration) {
        Instance instance = new Instance();
        instance.setIp(registration.getHost());
        instance.setPort(registration.getPort());
        instance.setWeight(this.nacosDiscoveryProperties.getWeight());
        instance.setClusterName(this.nacosDiscoveryProperties.getClusterName());
        instance.setMetadata(registration.getMetadata());
        return instance;
    }

    private Boolean isRegisterEnabled(Environment environment) {
        return environment.getProperty("ly.nacos.registry.enabled", Boolean.class, Boolean.TRUE);
    }
}
