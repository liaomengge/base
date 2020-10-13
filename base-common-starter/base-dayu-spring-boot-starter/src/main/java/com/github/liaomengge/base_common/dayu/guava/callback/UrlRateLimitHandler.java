package com.github.liaomengge.base_common.dayu.guava.callback;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liaomengge on 2019/11/7.
 */
public interface UrlRateLimitHandler {
    void blocked(HttpServletRequest request, HttpServletResponse response);
}
