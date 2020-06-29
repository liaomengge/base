package cn.mwee.base_common.utils.url;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Map;

/**
 * Created by liaomengge on 16/11/29.
 */
public final class MwUrlUtil {

    private MwUrlUtil() {
    }

    private static final String URL_SEPARATOR = "&";

    public static String rebuildUrl(String url, String parameters) {
        return checkUrl(url) + parameters;
    }

    public static String rebuildUrl(String url, Map<String, String> parameters) {
        return checkUrl(url) + StringUtils.join(parameters.entrySet(), URL_SEPARATOR);
    }

    public static String rebuildUrl(String url, List<NameValuePair> parameters) {
        StringBuilder sBuiler = new StringBuilder(16);

        NameValuePair nameValuePair;
        for (int i = 0, size = parameters.size(); i < size; i++) {
            nameValuePair = parameters.get(i);
            sBuiler.append(nameValuePair.getName()).append("=").append(nameValuePair.getValue());
            if (i != size - 1) {
                sBuiler.append(URL_SEPARATOR);
            }
        }

        return checkUrl(url) + sBuiler.toString();
    }

    private static String checkUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return StringUtils.EMPTY;
        }

        if (url.endsWith("/")) {
            return checkUrl(url.substring(0, url.length() - 1));
        }

        return url.lastIndexOf("?") != -1 ? url : url + "?";
    }
}
