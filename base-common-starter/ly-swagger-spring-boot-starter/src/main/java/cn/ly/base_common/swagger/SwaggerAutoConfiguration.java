package cn.ly.base_common.swagger;

import cn.ly.base_common.support.predicate._Predicates;
import cn.ly.base_common.swagger.SwaggerProperties.ApiInfoWrapper;
import cn.ly.base_common.swagger.annotation.EnableExtendSwaggerBootstrapUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.SpringfoxWebConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cn.ly.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2019/1/23.
 */
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@EnableSwagger2WebMvc
@EnableExtendSwaggerBootstrapUI
public class SwaggerAutoConfiguration {

    @Autowired
    private SwaggerProperties swaggerProperties;

    @Bean
    @ConditionalOnClass(SpringfoxWebConfiguration.class)
    public WebMvcConfigurer addResourceHandlers() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("swagger-ui.html", "doc.html")
                        .addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");
            }
        };
    }

    @Bean
    public Docket createRestApi() {
        String basePackage = this.swaggerProperties.getBasePackage();
        List<String> basePackageList = SPLITTER.splitToList(basePackage);
        List<Predicate<RequestHandler>> predicates =
                basePackageList.parallelStream().map(val -> RequestHandlerSelectors.basePackage(val)).collect(Collectors.toList());
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(this.swaggerProperties.getGroupName())
                .apiInfo(this.apiInfo())
                .select()
                .apis(_Predicates.or(predicates))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        ApiInfoWrapper api = this.swaggerProperties.getApi();
        return new ApiInfoBuilder()
                .title(api.getTitle())
                .description(api.getDescription())
                .termsOfServiceUrl(api.getTermsOfServiceUrl())
                .license(api.getLicense())
                .licenseUrl(api.getLicenseUrl())
                .version(api.getVersion())
                .contact(new Contact(api.getContact().getName(), api.getContact().getUrl(),
                        api.getContact().getEmail()))
                .extensions(api.getVendorExtensions())
                .build();
    }
}
