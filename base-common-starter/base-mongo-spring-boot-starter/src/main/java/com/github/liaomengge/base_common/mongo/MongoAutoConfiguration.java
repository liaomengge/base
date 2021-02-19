package com.github.liaomengge.base_common.mongo;

import com.mongodb.ConnectionString;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by liaomengge on 2018/11/7.
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MongoDatabaseFactory.class)
@EnableConfigurationProperties(MongoProperties.class)
public class MongoAutoConfiguration {

    private final MongoProperties mongoProperties;

    @Bean
    @ConditionalOnClass(ConnectionString.class)
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoProperties.getUri());
    }

    @Bean
    public DefaultMongoTypeMapper defaultMongoTypeMapper() {
        return new DefaultMongoTypeMapper(null);
    }

    private Set<Class<?>> scanForEntities(String basePackage) throws ClassNotFoundException {
        if (StringUtils.isBlank(basePackage)) {
            return Collections.emptySet();
        }

        Set<Class<?>> initialEntitySet = new HashSet<>();

        if (StringUtils.isNoneBlank(basePackage)) {

            ClassPathScanningCandidateComponentProvider componentProvider = new
                    ClassPathScanningCandidateComponentProvider(
                    false);
            componentProvider.addIncludeFilter(new AnnotationTypeFilter(Document.class));
            componentProvider.addIncludeFilter(new AnnotationTypeFilter(Persistent.class));

            for (BeanDefinition candidate : componentProvider.findCandidateComponents(basePackage)) {
                initialEntitySet.add(ClassUtils.forName(candidate.getBeanClassName(),
                        AbstractMongoClientConfiguration.class.getClassLoader()));
            }
        }
        return initialEntitySet;
    }

    private Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {
        Set<Class<?>> initialEntitySet = new HashSet<>();
        for (String basePackage : this.mongoProperties.getBasePackages()) {
            initialEntitySet.addAll(scanForEntities(basePackage));
        }
        return initialEntitySet;
    }

    @Bean
    public MongoMappingContext mongoMappingContext() throws ClassNotFoundException {
        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setInitialEntitySet(getInitialEntitySet());
        return mappingContext;
    }

    @Bean
    public CustomConversions customConversions() {
        return new MongoCustomConversions(Collections.emptyList());
    }

    @Bean
    @ConditionalOnClass({MongoDatabaseFactory.class, MongoMappingContext.class, DefaultMongoTypeMapper.class})
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory mongoDatabaseFactory,
                                                       MongoMappingContext mongoMappingContext,
                                                       DefaultMongoTypeMapper defaultMongoTypeMapper,
                                                       CustomConversions customConversions) {
        MappingMongoConverter mappingMongoConverter =
                new MappingMongoConverter(new DefaultDbRefResolver(mongoDatabaseFactory), mongoMappingContext);
        mappingMongoConverter.setTypeMapper(defaultMongoTypeMapper);
        mappingMongoConverter.setCustomConversions(customConversions);
        mappingMongoConverter.setCodecRegistryProvider(mongoDatabaseFactory);
        return mappingMongoConverter;
    }

    @Bean
    @ConditionalOnClass({MongoDatabaseFactory.class, MappingMongoConverter.class})
    @ConditionalOnMissingBean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory,
                                       MappingMongoConverter mappingMongoConverter) {
        return new MongoTemplate(mongoDatabaseFactory, mappingMongoConverter);
    }
}
