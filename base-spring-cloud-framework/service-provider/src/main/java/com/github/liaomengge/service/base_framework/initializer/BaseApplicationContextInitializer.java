package com.github.liaomengge.service.base_framework.initializer;

import com.github.liaomengge.service.base_framework.util.PrintLayoutUtil;
import com.taobao.text.ui.TableElement;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Created by liaomengge on 2019/5/27.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BaseApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        //BootstrapApplicationListener触发ApplicationEnvironmentPreparedEvent,会在Banner之前打印一遍,故需要过滤
        if (!(applicationContext instanceof AnnotationConfigApplicationContext)) {
            TableElement tableElement = PrintLayoutUtil.buildTableStyle();
            String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
            PrintLayoutUtil.addRowElement(tableElement, "Application: ", applicationName);
            PrintLayoutUtil.addRowElement(tableElement, "Github: ", "https://github.com/liaomengge/base");
            PrintLayoutUtil.addRowElement(tableElement, "Doc: ", "https://liaomengge.github.io/docsify");
            System.out.println(PrintLayoutUtil.render(tableElement));
        }
    }
}
