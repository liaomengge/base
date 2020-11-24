package com.github.liaomengge.base_common.framework.configuration.xss.filter;

import com.github.liaomengge.base_common.framework.configuration.xss.wrapper.XssHttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by liaomengge on 2020/10/17.
 */
public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper(request);
        filterChain.doFilter(xssHttpServletRequestWrapper, servletResponse);
    }
}
