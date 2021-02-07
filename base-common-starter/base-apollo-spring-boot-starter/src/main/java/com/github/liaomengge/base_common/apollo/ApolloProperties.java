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
     * 针对@RefreshScope注解刷新指定的keys
     */
    @Data
    public static class RefreshScopeProperties {
        private Set<String> changeKeys = Sets.newHashSet();
    }

    /**
     * 只针对@Bean @ConditionalOnProperty注解动态刷新符合条件的bean
     * 注：
     * 1. 被调用的bean，不能被@Autowired(required = false)注解
     */
    @Data
    public static class ConditionalProperties {
        private boolean enabled = false;
        private List<ClassConditionalProperties> classConditionals = Lists.newArrayList();
    }

    @Data
    public static class ClassConditionalProperties {
        private String scanClassName;
        private List<BeanConditionalProperties> beanConditionals = Lists.newArrayList();
    }

    @Data
    public static class BeanConditionalProperties {
        private String methodName;
        private String beanName;
    }
}
