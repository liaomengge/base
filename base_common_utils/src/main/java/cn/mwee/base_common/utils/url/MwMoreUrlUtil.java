package cn.mwee.base_common.utils.url;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by liaomengge on 16/11/29.
 */
public final class MwMoreUrlUtil {

    private MwMoreUrlUtil() {
    }

    public static String getUrlSuffix(String url) {
        if (StringUtils.isBlank(url)) {
            return StringUtils.EMPTY;
        }

        if (url.endsWith("/")) {
            return getUrlSuffix(url.substring(0, url.length() - 1));
        }

        return StringUtils.substring(url, url.lastIndexOf("/") + 1);
    }
}
