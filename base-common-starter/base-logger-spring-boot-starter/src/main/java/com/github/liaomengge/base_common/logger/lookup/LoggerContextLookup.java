package com.github.liaomengge.base_common.logger.lookup;

import com.github.liaomengge.base_common.utils.properties.LyResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by liaomengge on 2020/11/9.
 * <p>
 * 需要在log4j2的Configuration标签下配置“packages”才会生效
 */
@Plugin(name = "logger", category = StrLookup.CATEGORY)
public class LoggerContextLookup extends AbstractLookup {

    private static final String BOOTSTRAP_PROPERTIES_FILE_NAME = "bootstrap.properties";
    private static final String BOOTSTRAP_YML_FILE_NAME = "bootstrap.yml";
    private static final String PROPERTIES_FILE_NAME = "application.properties";
    private static final String YML_FILE_NAME = "application.yml";

    private static final String SPRING_APPLICATION_NAME = "spring.application.name";

    private static final Map<String, String> springContextMap = new HashMap<>(4);

    static {
        String springApplicationName;
        Properties properties = LyResourceUtil.loadProperties(BOOTSTRAP_PROPERTIES_FILE_NAME);
        springApplicationName = getSpringApplicationName(properties);
        if (StringUtils.isBlank(springApplicationName)) {
            properties = LyResourceUtil.loadProperties(BOOTSTRAP_YML_FILE_NAME);
            springApplicationName = getSpringApplicationName(properties);
            if (StringUtils.isBlank(springApplicationName)) {
                properties = LyResourceUtil.loadProperties(PROPERTIES_FILE_NAME);
                springApplicationName = getSpringApplicationName(properties);
                if (StringUtils.isBlank(springApplicationName)) {
                    properties = LyResourceUtil.loadYml(YML_FILE_NAME);
                    springApplicationName = getSpringApplicationName(properties);
                }
            }
        }
        springContextMap.put("springApplicationName", StringUtils.defaultIfBlank(springApplicationName, "base"));
    }

    private static String getSpringApplicationName(Properties properties) {
        return Optional.ofNullable(properties).map(val -> val.getProperty(SPRING_APPLICATION_NAME)).orElse(null);
    }

    @Override
    public String lookup(LogEvent logEvent, String key) {
        return springContextMap.get(key);
    }
}
