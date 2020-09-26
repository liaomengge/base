package cn.ly.base_common.base.mybatis.registry;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

import cn.ly.base_common.base.mybatis.MybatisProperties;
import cn.ly.base_common.base.mybatis.aspect.MybatisPointcutAdvisor;
import cn.ly.base_common.utils.binder.LyBinderUtil;

import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * Created by liaomengge on 2019/7/5.
 */
public class MybatisPointcutBeanDefinitionRegistry implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        MybatisProperties mybatisProperties = LyBinderUtil.bind((ConfigurableEnvironment) environment, "ly.mybatis",
                MybatisProperties.class);
        Optional.ofNullable(mybatisProperties).map(MybatisProperties::getMapping).ifPresent(mappingPropertiesList -> {
            mappingPropertiesList.forEach(mappingProperties -> {
                BeanDefinitionBuilder builder =
                        BeanDefinitionBuilder.genericBeanDefinition(MybatisPointcutAdvisor.class);
                builder.addConstructorArgValue(mappingProperties.getDsKeys());
                builder.addConstructorArgValue(mappingProperties.isDefaultMaster());
                builder.addConstructorArgValue(mappingProperties.getMapperPackage());
                BeanDefinition beanDefinition = builder.getRawBeanDefinition();
                beanDefinition.setScope(SCOPE_SINGLETON);
                BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition,
                        mappingProperties.getMapperPackage() + ".MybatisPointcutAdvisor");
                BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, registry);
            });
        });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
