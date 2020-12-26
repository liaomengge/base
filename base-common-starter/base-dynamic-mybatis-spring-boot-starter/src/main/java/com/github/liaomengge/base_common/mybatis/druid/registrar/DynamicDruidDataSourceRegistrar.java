package com.github.liaomengge.base_common.mybatis.druid.registrar;

import com.github.liaomengge.base_common.mybatis.druid.wrapper.DruidDataSourceWrapper;
import com.github.liaomengge.base_common.utils.binder.LyBinderUtil;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * Created by liaomengge on 2020/11/11.
 */
public class DynamicDruidDataSourceRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;
    private Map<String, DruidDataSourceWrapper> dataSourceMap;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;

        dataSourceMap = LyBinderUtil.bindMap(environment, "base.mybatis.durid", String.class,
                DruidDataSourceWrapper.class);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        dataSourceMap.forEach((key, value) -> {
            String beanName = key + "DataSource";
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSourceWrapper.class);
            builder.setInitMethodName("init");
            builder.setDestroyMethodName("close");
            BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getBeanDefinition(), beanName);
            BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        });
    }
}
