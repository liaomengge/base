package cn.ly.base_common.utils.text;

import cn.ly.base_common.utils.string.LyStringUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 17/11/8.
 */
@UtilityClass
public class LyEscapeJsonUtil {

    private final CharSequenceTranslator ESCAPE_JSON;

    static {
        Map<CharSequence, CharSequence> escapeJsonMap = new HashMap<>(16);
        escapeJsonMap.put("\"", "\\\"");
        escapeJsonMap.put("\\", "\\\\");
        escapeJsonMap.put("/", "\\/");
        ESCAPE_JSON = new AggregateTranslator(
                new LookupTranslator(Collections.unmodifiableMap(escapeJsonMap)),
                new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE)
        );
    }

    public final String escapeJson(String input) {
        try {
            return ESCAPE_JSON.translate(input);
        } catch (Exception e) {
            return LyStringUtil.replaceBlank(input);
        }
    }
}
