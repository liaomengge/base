package cn.ly.service.base_framework.util;

import lombok.experimental.UtilityClass;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by liaomengge on 2020/1/7.
 */
@UtilityClass
public class SpringBootUtils {

    public SpringApplicationBuilder create(Class<?>... sources) {
        return new SpringApplicationBuilder(sources);
    }

    public ConfigurableApplicationContext run(Class<?> source, String[] args) {
        return run(new Class<?>[]{source}, args);
    }

    public ConfigurableApplicationContext run(Class<?>[] sources, String[] args) {
        return create(sources).run(args);
    }

    public ConfigurableApplicationContext run(Class<?> source, String[] args, WebApplicationType webApplicationType) {
        return run(new Class<?>[]{source}, args, webApplicationType);
    }

    public ConfigurableApplicationContext run(Class<?>[] sources, String[] args,
                                              WebApplicationType webApplicationType) {
        return create(sources).web(webApplicationType).run(args);
    }
}
