package cn.ly.base_common.utils.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.UtilityClass;

import java.text.MessageFormat;
import java.util.concurrent.ThreadFactory;

/**
 * Created by liaomengge on 17/11/30.
 */
@UtilityClass
public class MwThreadFactoryBuilderUtil {

    private final String DEFAULT_NAME_FORMAT = "mwee-pool-{0}-thread-%d";

    public ThreadFactoryBuilder create() {
        return new ThreadFactoryBuilder();
    }

    public ThreadFactoryBuilder create(boolean daemon) {
        ThreadFactoryBuilder builder = create();
        builder.setDaemon(daemon);
        return builder;
    }

    public ThreadFactory build(String threadName) {
        ThreadFactoryBuilder builder = create();
        builder.setNameFormat(MessageFormat.format(DEFAULT_NAME_FORMAT, threadName));
        return builder.build();
    }

    public ThreadFactory build(String threadName, boolean daemon) {
        ThreadFactoryBuilder builder = create(daemon);
        builder.setNameFormat(MessageFormat.format(DEFAULT_NAME_FORMAT, threadName));
        return builder.build();
    }
}
