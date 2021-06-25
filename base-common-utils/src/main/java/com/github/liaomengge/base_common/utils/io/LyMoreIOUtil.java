package com.github.liaomengge.base_common.utils.io;

import com.google.common.io.CharStreams;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Created by liaomengge on 2018/7/27.
 */
@Slf4j
@UtilityClass
public class LyMoreIOUtil {
    
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
