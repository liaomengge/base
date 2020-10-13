package com.github.liaomengge.base_common.utils.validator;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 17/7/27.
 */
@UtilityClass
public class LyValidatorUtil {

    /**
     * 正则：手机号（简单）, 1字头＋10位数字即可.
     */
    private final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";
    private final Pattern PATTERN_REGEX_MOBILE_SIMPLE = Pattern.compile(REGEX_MOBILE_SIMPLE);

    /**
     * 正则：手机号（精确）, 已知3位前缀＋8位数字
     * <p>
     * 移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188
     * </p>
     * <p>
     * 联通：130、131、132、145、155、156、175、176、185、186
     * </p>
     * <p>
     * 电信：133、153、173、177、180、181、189
     * </p>
     * <p>
     * 全球星：1349
     * </p>
     * <p>
     * 虚拟运营商：170
     * </p>
     */
    private final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))" +
            "\\d{8}$";
    private final Pattern PATTERN_REGEX_MOBILE_EXACT = Pattern.compile(REGEX_MOBILE_EXACT);

    /**
     * 正则：固定电话号码, 可带区号, 然后6至少8位数字
     */
    private final String REGEX_TEL = "^(\\d{3,4}-)?\\d{6,8}$";
    private final Pattern PATTERN_REGEX_TEL = Pattern.compile(REGEX_TEL);

    /**
     * 正则：身份证号码15位, 数字且关于生日的部分必须正确
     */
    private final String REGEX_ID_CARD15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    private final Pattern PATTERN_REGEX_ID_CARD15 = Pattern.compile(REGEX_ID_CARD15);

    /**
     * 正则：身份证号码18位, 数字且关于生日的部分必须正确
     */
    private final String REGEX_ID_CARD18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}" +
            "([0-9Xx])$";
    private final Pattern PATTERN_REGEX_ID_CARD18 = Pattern.compile(REGEX_ID_CARD18);

    /**
     * 正则：邮箱, 有效字符(不支持中文), 且中间必须有@, 后半部分必须有.
     */
    private final String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    private final Pattern PATTERN_REGEX_EMAIL = Pattern.compile(REGEX_EMAIL);

    /**
     * 正则：URL, 必须有"://",前面必须是英文, 后面不能有空格
     */
    private final String REGEX_URL = "[a-zA-z]+://[^\\s]*";
    private final Pattern PATTERN_REGEX_URL = Pattern.compile(REGEX_URL);

    /**
     * 正则：yyyy-MM-dd格式的日期校验, 已考虑平闰年
     */
    private final String REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|" +
            "(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|" +
            "(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
    private final Pattern PATTERN_REGEX_DATE = Pattern.compile(REGEX_DATE);

    /**
     * 正则：IP地址
     */
    private final String REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
    private final Pattern PATTERN_REGEX_IP = Pattern.compile(REGEX_IP);

    /**
     * 验证手机号（简单）
     */
    public boolean isMobileSimple(CharSequence input) {
        return isMatch(PATTERN_REGEX_MOBILE_SIMPLE, input);
    }

    /**
     * 验证手机号（精确）
     */
    public boolean isMobileExact(CharSequence input) {
        return isMatch(PATTERN_REGEX_MOBILE_EXACT, input);
    }

    /**
     * 验证固定电话号码
     */
    public boolean isTel(CharSequence input) {
        return isMatch(PATTERN_REGEX_TEL, input);
    }

    /**
     * 验证15或18位身份证号码
     */
    public boolean isIdCard(CharSequence input) {
        return isMatch(PATTERN_REGEX_ID_CARD15, input) || isMatch(PATTERN_REGEX_ID_CARD18, input);
    }

    /**
     * 验证邮箱
     */
    public boolean isEmail(CharSequence input) {
        return isMatch(PATTERN_REGEX_EMAIL, input);
    }

    /**
     * 验证URL
     */
    public boolean isUrl(CharSequence input) {
        return isMatch(PATTERN_REGEX_URL, input);
    }

    /**
     * 验证yyyy-MM-dd格式的日期校验, 已考虑平闰年
     */
    public boolean isDate(CharSequence input) {
        return isMatch(PATTERN_REGEX_DATE, input);
    }

    /**
     * 验证IP地址
     */
    public boolean isIp(CharSequence input) {
        return isMatch(PATTERN_REGEX_IP, input);
    }

    public boolean isMatch(Pattern pattern, CharSequence input) {
        return StringUtils.isNotEmpty(input) && pattern.matcher(input).matches();
    }
}
