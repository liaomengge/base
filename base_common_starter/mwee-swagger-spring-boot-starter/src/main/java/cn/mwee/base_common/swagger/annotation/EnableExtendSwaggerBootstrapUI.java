package cn.mwee.base_common.swagger.annotation;

import cn.mwee.base_common.swagger.security.ExtendSecurityConfiguration;
import com.github.xiaoymin.swaggerbootstrapui.configuration.MarkdownFileConfiguration;
import com.github.xiaoymin.swaggerbootstrapui.configuration.SwaggerBootstrapUIConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2019/7/12.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SwaggerBootstrapUIConfiguration.class, ExtendSecurityConfiguration.class, MarkdownFileConfiguration.class})
public @interface EnableExtendSwaggerBootstrapUI {
}
