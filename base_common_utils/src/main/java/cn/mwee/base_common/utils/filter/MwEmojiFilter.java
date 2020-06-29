package cn.mwee.base_common.utils.filter;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by liaomengge on 16/8/1.
 */
public final class MwEmojiFilter {

    private static final String regex = "[\\x{10000}-\\x{10ffff}\\ud800-\\udfff]";

    private MwEmojiFilter() {
    }

    public static String filterEmoji(String str) {
        return filterEmoji(str, "*");
    }

    public static String filterEmoji(String str, String replacement) {
        if (StringUtils.isNotBlank(str)) {
            return str.replaceAll(regex, replacement);
        }
        return StringUtils.defaultString(str, "");
    }

}
