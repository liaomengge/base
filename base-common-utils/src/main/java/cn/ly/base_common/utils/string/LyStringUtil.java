package cn.ly.base_common.utils.string;

import cn.ly.base_common.support.misc.Charsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 17/11/8.
 */
@UtilityClass
public class LyStringUtil {

    private final Pattern p = Pattern.compile("[\t|\r|\n]");
    private final Pattern p2 = Pattern.compile("[\"|\\\\]");

    public String getValue(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        return str;
    }

    public String getValue(Object str) {
        if (str == null) {
            return "";
        }

        if (StringUtils.isBlank(str.toString())) {
            return "";
        }
        return str.toString();
    }

    public boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    public boolean isBlank(Object str) {
        return StringUtils.isBlank(getValue(str));
    }

    public String replaceBlank(String str) {
        String dest = "";
        if (!isBlank(str)) {
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public String replaceQuota(String str) {
        String dest = "";
        if (!isBlank(str)) {
            Matcher m = p2.matcher(str);
            dest = m.replaceAll("'").replace("''", "'");
        }
        return dest;
    }

    public String replace(String text) {
        return replace(text, "", "\\\"");
    }

    public String replace(String text, String searchString, String replaceString) {
        return StringUtils.replace(text, searchString, replaceString);
    }

    /**
     * 高性能的Split, 针对char的分隔符号, 比JDK String自带的高效.
     * <p>
     * from Commons Lange 3.5 StringUtils, 做如下优化:
     * <p>
     * 1. 最后不做数组转换, 直接返回List.
     * <p>
     * 2. 可设定List初始大小.
     * <p>
     * 3. preserveAllTokens 取默认值false
     *
     * @return 如果为null返回null, 如果为""返回空数组
     *
     * @param str
     * @param separatorChar
     * @param expectParts
     * @return
     */
    public List<String> split(String str, char separatorChar, int expectParts) {
        if (str == null) {
            return null;
        }

        int len = str.length();
        if (len == 0) {
            return Collections.EMPTY_LIST;
        }
        List<String> list = new ArrayList<>(expectParts);
        int i = 0;
        int start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }
                start = ++i;
                continue;
            }
            match = true;
            i++;
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return list;
    }

    /**
     * String 有replace(char,char), 但缺少单独replace first/last的
     */
    public String replaceFirst(String s, char sub, char with) {
        if (s == null) {
            return null;
        }
        int index = s.indexOf(sub);
        if (index == -1) {
            return s;
        }
        char[] str = s.toCharArray();
        str[index] = with;
        return new String(str);
    }

    /**
     * String 有replace(char,char)替换全部char, 但缺少单独replace first/last
     */
    public String replaceLast(String s, char sub, char with) {
        if (s == null) {
            return null;
        }

        int index = s.lastIndexOf(sub);
        if (index == -1) {
            return s;
        }
        char[] str = s.toCharArray();
        str[index] = with;
        return new String(str);
    }

    /**
     * 判断字符串是否以字母开头
     * <p>
     * 如果字符串为Null或空, 返回false
     *
     * @param s
     * @param c
     * @return
     */
    public boolean startWith(CharSequence s, char c) {
        if (StringUtils.isEmpty(s)) {
            return false;
        }
        return s.charAt(0) == c;
    }

    /**
     * 判断字符串是否以字母结尾
     * <p>
     * 如果字符串为Null或空, 返回false
     */
    public boolean endWith(CharSequence s, char c) {
        if (StringUtils.isEmpty(s)) {
            return false;
        }
        return s.charAt(s.length() - 1) == c;
    }

    /**
     * 比较字符串, 避免字符串因为过长, 产生耗时
     *
     * @param a String
     * @param b String
     * @return 是否相同
     */
    public boolean slowEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return slowEquals(a.getBytes(Charsets.UTF_8), b.getBytes(Charsets.UTF_8));
    }

    /**
     * 比较 byte 数组, 避免字符串因为过长, 产生耗时
     *
     * @param a byte array
     * @param b byte array
     * @return 是否相同
     */
    public boolean slowEquals(byte[] a, byte[] b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.length != b.length) {
            return false;
        }
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    /**
     * 首字母变小写
     *
     * @param str 字符串
     * @return {String}
     */
    public String firstCharToLower(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * 首字母变大写
     *
     * @param str 字符串
     * @return {String}
     */
    public String firstCharToUpper(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return str;
    }
}
