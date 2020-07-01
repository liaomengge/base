package cn.ly.base_common.utils.url;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by liaomengge on 16/11/29.
 */
@UtilityClass
public class LyMoreUrlUtil {

    public String getUrlSuffix(String url) {
        if (StringUtils.isBlank(url)) {
            return StringUtils.EMPTY;
        }

        if (url.endsWith("/")) {
            return getUrlSuffix(url.substring(0, url.length() - 1));
        }

        return StringUtils.substring(url, url.lastIndexOf("/") + 1);
    }
}
