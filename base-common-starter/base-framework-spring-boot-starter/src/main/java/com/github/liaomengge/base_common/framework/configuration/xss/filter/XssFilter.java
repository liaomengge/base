package com.github.liaomengge.base_common.framework.configuration.xss.filter;

import com.github.liaomengge.base_common.framework.configuration.xss.wrapper.XssHttpServletRequestWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liaomengge on 2020/10/17.
 */
public class XssFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request instanceof XssHttpServletRequestWrapper) {
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(new XssHttpServletRequestWrapper(request), response);
    }
}
