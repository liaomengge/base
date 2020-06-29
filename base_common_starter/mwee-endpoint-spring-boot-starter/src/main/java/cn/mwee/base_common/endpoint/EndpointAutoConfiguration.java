package cn.mwee.base_common.endpoint;

import cn.mwee.base_common.endpoint.info.CustomInfoConfiguration;
import org.springframework.boot.actuate.autoconfigure.InfoContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2019/7/4.
 */
@Configuration
@AutoConfigureBefore(InfoContributorAutoConfiguration.class)
@AutoConfigureAfter(ProjectInfoAutoConfiguration.class)
@Import(CustomInfoConfiguration.class)
public class EndpointAutoConfiguration {
}
