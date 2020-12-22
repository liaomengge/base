package com.github.liaomengge.base_common.ribbon;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2020/12/22.
 */
@Data
@ConfigurationProperties("base.ribbon")
public class RibbonProperties {

    private EagerLoadProperties eagerLoad = new EagerLoadProperties();

    @Data
    public static class EagerLoadProperties {
        private boolean enabled = true;
    }
}
