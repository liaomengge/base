package cn.mwee.base_common.utils.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liaomengge on 17/10/11.
 */
public final class MwMatcherUtil {

    private MwMatcherUtil() {
    }

    @Deprecated
    public static boolean isMatch(String regex, String matchStr) {
        Matcher matcher = Pattern.compile(regex).matcher(matchStr);
        return matcher.find();
    }

    /**
     * 部分匹配
     *
     * {@link #isPartMatch(String, String)}
     * @param regex
     * @param matchStr
     * @return
     */
    @Deprecated
    public static boolean find(String regex, String matchStr) {
        return isMatch(regex, matchStr);
    }

    /**
     * 全部匹配
     *
     * {@link #isAllMatch(String, String)}
     * @param regex
     * @param matchStr
     * @return
     */
    @Deprecated
    public static boolean matches(String regex, String matchStr) {
        return matchStr.matches(regex);
    }

    /**
     * 部分匹配
     *
     * @param regex
     * @param matchStr
     * @return
     */
    public static boolean isPartMatch(String regex, String matchStr) {
        Matcher matcher = Pattern.compile(regex).matcher(matchStr);
        return matcher.find();
    }

    /**
     * 部分匹配
     *
     * @param pattern
     * @param matchStr
     * @return
     */
    public static boolean isPartMatch(Pattern pattern, String matchStr) {
        return pattern.matcher(matchStr).find();
    }

    /**
     * 全部匹配
     *
     * @param regex
     * @param matchStr
     * @return
     */
    public static boolean isAllMatch(String regex, String matchStr) {
        return matchStr.matches(regex);
    }

    /**
     * 全部匹配
     *
     * @param pattern
     * @param matchStr
     * @return
     */
    public static boolean isAllMatch(Pattern pattern, String matchStr) {
        return pattern.matcher(matchStr).matches();
    }
}
