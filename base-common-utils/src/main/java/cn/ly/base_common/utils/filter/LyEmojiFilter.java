package cn.ly.base_common.utils.filter;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by liaomengge on 16/8/1.
 */
@UtilityClass
public class LyEmojiFilter {

    private final String regex = "[\\x{10000}-\\x{10ffff}\\ud800-\\udfff]";

    public String filterEmoji(String str) {
        return filterEmoji(str, "*");
    }

    public String filterEmoji(String str, String replacement) {
        if (StringUtils.isNotBlank(str)) {
            return str.replaceAll(regex, replacement);
        }
        return StringUtils.defaultString(str, "");
    }

}
