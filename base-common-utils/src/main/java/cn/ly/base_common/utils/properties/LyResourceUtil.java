package cn.ly.base_common.utils.properties;

import cn.ly.base_common.utils.io.LyIOUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by liaomengge on 2020/8/1.
 */
@UtilityClass
public class LyResourceUtil {

    private final Logger log = LyLogger.getInstance(LyResourceUtil.class);

    public URL getResource(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResource(resourceName);
    }

    public InputStream getResourceAsStream(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    }

    public void loadProperties(String resourceName) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
            if (Objects.nonNull(inputStream)) {
                properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            log.warn("load file [" + resourceName + "] fail", e);
        } finally {
            LyIOUtil.closeQuietly(inputStream);
        }
    }
}
