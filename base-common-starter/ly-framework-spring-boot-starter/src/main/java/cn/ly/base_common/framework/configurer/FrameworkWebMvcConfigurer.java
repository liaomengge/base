package cn.ly.base_common.framework.configurer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Servlet;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by liaomengge on 2019/11/29.
 */
@Configuration
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication
public class FrameworkWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(x -> x instanceof StringHttpMessageConverter);
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringHttpMessageConverter.setWriteAcceptCharset(false);// see SPR-7316
        converters.add(0, stringHttpMessageConverter);
    }
}
