package com.github.liaomengge.base_common.swagger;

import static springfox.documentation.spring.web.plugins.Docket.DEFAULT_GROUP_NAME;

import com.google.common.collect.Lists;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;
import springfox.documentation.service.VendorExtension;

/**
 * Created by liaomengge on 2019/1/23.
 */
@Data
@Validated
@ConfigurationProperties("base.swagger")
public class SwaggerProperties {

    private boolean enabled;
    private AuthBasic basic;
    @NotNull
    private String basePackage;
    private String groupName = DEFAULT_GROUP_NAME;
    private ApiInfoWrapper api = new ApiInfoWrapper();

    @Data
    public class AuthBasic {
        private boolean enabled;
        private String username;
        private String password;
    }

    @Data
    public class ApiInfoWrapper {

        private String title;
        private String description;
        private String termsOfServiceUrl;
        private String license;
        private String licenseUrl;
        private String version;
        private ContactWrapper contact = new ContactWrapper();
        private List<VendorExtension> vendorExtensions = Lists.newArrayList();
    }

    @Data
    public static class ContactWrapper {
        private String name = "";
        private String url = "";
        private String email = "";
    }
}
