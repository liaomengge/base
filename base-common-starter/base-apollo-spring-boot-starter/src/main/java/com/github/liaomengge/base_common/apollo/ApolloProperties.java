package com.github.liaomengge.base_common.apollo;

import com.github.liaomengge.base_common.apollo.enums.RefreshTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Set;

/**
 * Created by liaomengge on 2021/1/29.
 */
@Data
@ConfigurationProperties("base.apollo")
public class ApolloProperties {

    private RefreshTypeEnum refreshType = RefreshTypeEnum.PROPERTIES;
    private RefreshScopeProperties refreshScope = new RefreshScopeProperties();
    private ConditionalProperties conditional = new ConditionalProperties();

    /**
     * 针对{@link RefreshTypeEnum#SCOPE}类型下，@RefreshScope注解刷新指定的keys
     */
    @Data
    public static class RefreshScopeProperties {
        private Set<String> changeKeys = Sets.newHashSet();
    }

    /**
     * 只针对包含{@link RefreshTypeEnum#PROPERTIES}类型下，@Bean @ConditionalOnProperty注解动态刷新符合条件的bean
     * 注：
     * 1. 被调用的bean，不能被@Autowired(required = false)注解，
     * 只能通过{@link org.springframework.context.ApplicationContext#getBean()获取}或者
     * {@link org.springframework.beans.factory.ObjectProvider}注入
     * 2. 只有在${spring-framework.version} >= 5.2.13.RELEASE或者${spring-framework.version} >=5.3.4.RELEASE，
     * 见github issue: https://github.com/spring-projects/spring-framework/issues/26518
     */
    @Data
    public static class ConditionalProperties {
        private boolean enabled = false;
        private List<ClassConditionalProperties> classConditionals = Lists.newArrayList();
    }

    /**
     * 指定扫描指定的类下面的所有@Bean @ConditionalOnProperty的对象
     */
    @Data
    public static class ClassConditionalProperties {
        private String scanClassName;
        private List<BeanConditionalProperties> beanConditionals = Lists.newArrayList();
    }

    /**
     * 指定scan class下，匹配相同的methodName的beanName，默认：返回类型的SimpleName
     */
    @Data
    public static class BeanConditionalProperties {
        private String methodName;
        private String beanName;
    }
}
