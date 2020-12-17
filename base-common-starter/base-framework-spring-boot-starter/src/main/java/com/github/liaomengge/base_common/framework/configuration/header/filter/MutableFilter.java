package com.github.liaomengge.base_common.framework.configuration.header.filter;

import com.github.liaomengge.base_common.framework.configuration.header.wrapper.MutableHttpServletRequestWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liaomengge on 2020/12/8.
 */
public class MutableFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request instanceof MutableHttpServletRequestWrapper) {
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(new MutableHttpServletRequestWrapper(request), response);
    }
}
