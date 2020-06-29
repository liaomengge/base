package cn.mwee.base_common.utils.io;

import cn.mwee.base_common.utils.log4j2.MwLogger;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by liaomengge on 2018/7/27.
 */
public final class MwMoreIOUtil {

    private static final Logger logger = MwLogger.getInstance(MwMoreIOUtil.class);

    private MwMoreIOUtil() {
    }

    public static String loadScript(String fileName) {
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName))) {
            return CharStreams.toString(reader);
        } catch (IOException e) {
            logger.error("加载文件[" + fileName + "]失败", e);
        }

        return null;
    }
}
