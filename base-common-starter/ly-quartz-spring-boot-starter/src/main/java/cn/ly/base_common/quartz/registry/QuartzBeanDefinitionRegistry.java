package cn.ly.base_common.quartz.registry;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

import cn.ly.base_common.quartz.domain.AbstractBaseJob;
import cn.ly.base_common.utils.log4j2.LyLogger;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;

/**
 * Created by liaomengge on 2019/1/29.
 */
public class QuartzBeanDefinitionRegistry implements EnvironmentAware, ApplicationContextAware,
        BeanDefinitionRegistryPostProcessor {

    private static final Logger log = LyLogger.getInstance(QuartzBeanDefinitionRegistry.class);

    private static final String JOB_PKG = "ly.quartz.basePackage";

    private Environment environment;
    private static ApplicationContext applicationContext;
    private static Set<String> jobBeanDefinitionSet = Sets.newConcurrentHashSet();
    private static Map<String, Object> jobBeanMap = Maps.newConcurrentMap();

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        QuartzBeanDefinitionRegistry.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathScanningCandidateComponentProvider beanScanner =
                new ClassPathScanningCandidateComponentProvider(false, this.environment);
        TypeFilter includeFilter = new AssignableTypeFilter(AbstractBaseJob.class);
        beanScanner.addIncludeFilter(includeFilter);
        Set<BeanDefinition> beanDefinitions = beanScanner.findCandidateComponents(this.buildBasePackage());
        for (BeanDefinition beanDefinition : beanDefinitions) {
            //beanName通常由对应的BeanNameGenerator来生成, 比如Spring自带的AnnotationBeanNameGenerator、DefaultBeanNameGenerator
            // 等, 也可以自己实现。
            String beanClassName = beanDefinition.getBeanClassName();
            beanDefinition.setScope(SCOPE_SINGLETON);
            registry.registerBeanDefinition(beanClassName, beanDefinition);
            jobBeanDefinitionSet.add(beanClassName);
        }
        log.info("job registered number[{}], details ===> {}", jobBeanDefinitionSet.size(),
                jobBeanDefinitionSet.toString());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    private String buildBasePackage() {
        String key = JOB_PKG;
        String value = environment.getProperty(key);
        if (StringUtils.isEmpty(value)) {
            key = LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, key);
            value = environment.getProperty(key);
        }

        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("property " + key + " can not find!!!");
        }
        return value;
    }

    public static Map<String, Object> getJobBeanMap() {
        Map<String, AbstractBaseJob> baseJobMap = applicationContext.getBeansOfType(AbstractBaseJob.class);
        baseJobMap.values().forEach(val -> {
            String className = val.getClass().getName();
            if (jobBeanDefinitionSet.contains(className)) {
                jobBeanMap.put(className, val);
            }
        });
        return jobBeanMap;
    }
}
