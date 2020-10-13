package com.github.liaomengge.base_common.utils.unicode;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LyUnicodeUtil {

    private Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
    private Pattern p2 = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

    public String decodeUnicode(String str) {
        Charset set = Charset.forName("UTF-16");
        Matcher m = p.matcher(str);
        int start = 0;
        int start2 = 0;
        StringBuffer sb = new StringBuffer();
        for (; m.find(start); start = m.end()) {
            start2 = m.start();
            if (start2 > start) {
                String seg = str.substring(start, start2);
                sb.append(seg);
            }
            String code = m.group(1);
            int i = Integer.valueOf(code, 16).intValue();
            byte[] bb = new byte[4];
            bb[0] = (byte) (i >> 8 & 0xff);
            bb[1] = (byte) (i & 0xff);
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append(String.valueOf(set.decode(b)).trim());
        }

        start2 = str.length();
        if (start2 > start) {
            String seg = str.substring(start, start2);
            sb.append(seg);
        }
        return sb.toString();
    }

    public String stringToUnicode(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            if (ch > 255) {
                str = (new StringBuilder(String.valueOf(str))).append("\\u").append(Integer.toHexString(ch)).toString();
            } else {
                str = (new StringBuilder(String.valueOf(str))).append("\\u00").append(Integer.toHexString(ch)).toString();
            }
        }

        return str;
    }

    public String unicodeToString(String str) {
        for (Matcher matcher = p2.matcher(str); matcher.find(); ) {
            char ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), String.valueOf(ch));
        }

        return str;
    }

}
