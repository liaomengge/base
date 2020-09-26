package cn.ly.base_common.utils.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 17/10/11.
 */
@UtilityClass
public class LyMatcherUtil {

    @Deprecated
    public boolean isMatch(String regex, String matchStr) {
        Matcher matcher = Pattern.compile(regex).matcher(matchStr);
        return matcher.find();
    }

    /**
     * 部分匹配
     * <p>
     * {@link #isPartMatch(String, String)}
     *
     * @param regex
     * @param matchStr
     * @return
     */
    @Deprecated
    public boolean find(String regex, String matchStr) {
        return isMatch(regex, matchStr);
    }

    /**
     * 全部匹配
     * <p>
     * {@link #isAllMatch(String, String)}
     *
     * @param regex
     * @param matchStr
     * @return
     */
    @Deprecated
    public boolean matches(String regex, String matchStr) {
        return matchStr.matches(regex);
    }

    /**
     * 部分匹配
     *
     * @param regex
     * @param matchStr
     * @return
     */
    public boolean isPartMatch(String regex, String matchStr) {
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
    public boolean isPartMatch(Pattern pattern, String matchStr) {
        return pattern.matcher(matchStr).find();
    }

    /**
     * 全部匹配
     *
     * @param regex
     * @param matchStr
     * @return
     */
    public boolean isAllMatch(String regex, String matchStr) {
        return matchStr.matches(regex);
    }

    /**
     * 全部匹配
     *
     * @param pattern
     * @param matchStr
     * @return
     */
    public boolean isAllMatch(Pattern pattern, String matchStr) {
        return pattern.matcher(matchStr).matches();
    }
}
