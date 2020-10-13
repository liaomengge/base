package com.github.liaomengge.base_common.dayu.guava.callback;

import com.github.liaomengge.base_common.dayu.domain.DayuBlockedDomain;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;

import java.util.Objects;

/**
 * Created by liaomengge on 2019/11/7.
 */
public class WebCallbackManager {
    private static volatile UrlRateLimitHandler urlRateLimitHandler =
            (request, response) -> LyWebUtil.renderJson(response, DayuBlockedDomain.create());

    private WebCallbackManager() {
    }

    public static UrlRateLimitHandler getUrlRateLimitHandler() {
        return urlRateLimitHandler;
    }

    public static void setUrlBlockHandler(UrlRateLimitHandler urlRateLimitHandler2) {
        Objects.requireNonNull(urlRateLimitHandler, "URL rate limit handler should not be null");
        urlRateLimitHandler = urlRateLimitHandler2;
    }
}
