package cn.ly.base_common.swagger;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import springfox.documentation.service.VendorExtension;

import javax.validation.constraints.NotNull;
import java.util.List;

import static springfox.documentation.spring.web.plugins.Docket.DEFAULT_GROUP_NAME;

/**
 * Created by liaomengge on 2019/1/23.
 */
@Data
@Validated
@ConfigurationProperties("ly.swagger")
public class SwaggerProperties {

    private boolean enabled;
    @NotNull
    private String basePackage;
    private String groupName = DEFAULT_GROUP_NAME;
    private ApiInfoWrapper api = new ApiInfoWrapper();

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
