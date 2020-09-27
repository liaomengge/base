package cn.ly.base_common.utils.io;

import cn.ly.base_common.utils.log4j2.LyLogger;
import com.google.common.io.CharStreams;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Created by liaomengge on 2018/7/27.
 */
@UtilityClass
public class LyMoreIOUtil {

    private final Logger log = LyLogger.getInstance(LyMoreIOUtil.class);

    public String loadScript(String fileName) {
        try (Reader reader =
                     new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName), Charset.defaultCharset())) {
            return CharStreams.toString(reader);
        } catch (IOException e) {
            log.error("加载文件[" + fileName + "]失败", e);
        }

        return null;
    }
}
