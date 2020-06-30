package cn.ly.base_common.dayu.guava.callback;

import cn.ly.base_common.dayu.domain.DayuBlockedDomain;
import cn.ly.base_common.utils.web.MwWebUtil;

import java.util.Objects;

/**
 * Created by liaomengge on 2019/11/7.
 */
public class WebCallbackManager {
    private static volatile UrlRateLimitHandler urlRateLimitHandler =
            (request, response) -> MwWebUtil.renderJson(response, DayuBlockedDomain.create());

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
