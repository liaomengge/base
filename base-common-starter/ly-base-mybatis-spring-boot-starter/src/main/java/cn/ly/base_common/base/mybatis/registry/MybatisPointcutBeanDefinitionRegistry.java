package cn.ly.base_common.base.mybatis.registry;

import cn.ly.base_common.base.mybatis.MybatisProperties;
import cn.ly.base_common.base.mybatis.aspect.MybatisPointcutAdvisor;
import cn.ly.base_common.utils.json.MwJsonUtil;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.bind.PropertySourcesPropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

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
        Map<String, LinkedHashMap<String, Object>> mappingPropertiesMap = Maps.newHashMap();
        MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
        new RelaxedDataBinder(mappingPropertiesMap, "ly.mybatis").bind(new PropertySourcesPropertyValues(propertySources));
        LinkedHashMap<String, Object> subMappingPropertiesMap = mappingPropertiesMap.get("mapping");
        subMappingPropertiesMap = subMappingPropertiesMap.entrySet().stream()
                .filter(val -> val.getValue() instanceof LinkedHashMap && StringUtils.isNumeric(val.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal,
                        LinkedHashMap::new));
        String mappingJson = MwJsonUtil.toJson(subMappingPropertiesMap);
        Map<String, MybatisProperties.MappingProperties> mappingJsonMap = MwJsonUtil.fromJson(mappingJson,
                new TypeReference<Map<String, MybatisProperties.MappingProperties>>() {
                });
        Optional.ofNullable(mappingJsonMap).ifPresent(val -> val.values().forEach(mappingProperties -> {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MybatisPointcutAdvisor.class);
            builder.addConstructorArgValue(mappingProperties.getDsKeys());
            builder.addConstructorArgValue(mappingProperties.isDefaultMaster());
            builder.addConstructorArgValue(mappingProperties.getMapperPackage());
            BeanDefinition beanDefinition = builder.getRawBeanDefinition();
            beanDefinition.setScope(SCOPE_SINGLETON);
            BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition,
                    mappingProperties.getMapperPackage() + ".MybatisPointcutAdvisor");
            BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, registry);
        }));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
