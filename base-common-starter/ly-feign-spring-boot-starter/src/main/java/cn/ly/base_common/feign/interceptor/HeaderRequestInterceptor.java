package cn.ly.base_common.feign.interceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Created by liaomengge on 2020/8/25.
 */
public class HeaderRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String values = request.getHeader(name);
                requestTemplate.header(name, values);
            }
        }

        Enumeration<String> reqAttributeNames = request.getAttributeNames();
        if (reqAttributeNames != null) {
            while (reqAttributeNames.hasMoreElements()) {
                String attrName = reqAttributeNames.nextElement();
                String values = request.getAttribute(attrName).toString();
                requestTemplate.header(attrName, values);
            }
        }
    }
}
