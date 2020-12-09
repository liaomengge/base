package com.github.liaomengge.base_common.framework.configuration.header.filter;

import com.github.liaomengge.base_common.framework.configuration.header.wrapper.MutableHttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by liaomengge on 2020/12/8.
 */
public class MutableFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (request instanceof MutableHttpServletRequestWrapper) {
            chain.doFilter(request, response);
            return;
        }
        chain.doFilter(new MutableHttpServletRequestWrapper((HttpServletRequest) request), response);
    }
}
