package cn.mwee.service.base_framework.util;

import lombok.experimental.UtilityClass;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by liaomengge on 2020/1/7.
 */
@UtilityClass
public class SpringBootUtils {

    public SpringApplicationBuilder create(Object[] sources) {
        return new SpringApplicationBuilder(sources);
    }

    public ConfigurableApplicationContext run(Object source, String[] args) {
        return run(new Object[]{source}, args);
    }

    public ConfigurableApplicationContext run(Object[] sources, String[] args) {
        return create(sources).run(args);
    }

    public ConfigurableApplicationContext run(Object source, String[] args, boolean webEnvironment) {
        return run(new Object[]{source}, args, webEnvironment);
    }

    public ConfigurableApplicationContext run(Object[] sources, String[] args, boolean webEnvironment) {
        return create(sources).web(webEnvironment).run(args);
    }
}
