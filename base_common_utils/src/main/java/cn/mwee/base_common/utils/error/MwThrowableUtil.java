package cn.mwee.base_common.utils.error;

import cn.mwee.base_common.utils.string.MwStringUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by liaomengge on 17/11/8.
 */
public final class MwThrowableUtil {

    private MwThrowableUtil() {
    }

    /**
     * 获取异常的完整信息
     *
     * @param t
     * @return
     */
    public static String getStackTrace(Throwable t) {
        return getStackTrace(t, true, -1);
    }

    /**
     * 获取异常的部分信息
     * length elt; 0 表示获取完整的长度
     *
     * @param t
     * @param length
     * @return
     */
    public static String getStackTrace(Throwable t, int length) {
        return getStackTrace(t, true, length);
    }

    public static String getStackTrace(Throwable t, boolean removeLF) {
        return getStackTrace(t, removeLF, -1);
    }

    public static String getStackTrace(Throwable t, boolean removeLF, int length) {
        if (t == null) {
            return "";
        }

        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw);
        try {
            t.printStackTrace(pw);
            String stackTrace = sw.toString();
            if (removeLF) {
                stackTrace = MwStringUtil.replaceBlank(stackTrace);
            }
            if (length <= 0) {
                return stackTrace;
            }
            int len = stackTrace.length() >= length ? length : stackTrace.length();
            return StringUtils.substring(stackTrace, 0, len);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
}
