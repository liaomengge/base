package cn.mwee.base_common.utils.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.text.MessageFormat;
import java.util.concurrent.ThreadFactory;

/**
 * Created by liaomengge on 17/11/30.
 */
public final class MwThreadFactoryBuilderUtil {

    private static final String DEFAULT_NAME_FORMAT = "mwee-pool-{0}-thread-%d";

    private MwThreadFactoryBuilderUtil() {
    }

    public static ThreadFactoryBuilder create() {
        return new ThreadFactoryBuilder();
    }

    public static ThreadFactoryBuilder create(boolean daemon) {
        ThreadFactoryBuilder builder = create();
        builder.setDaemon(daemon);
        return builder;
    }

    public static ThreadFactory build(String threadName) {
        ThreadFactoryBuilder builder = create();
        builder.setNameFormat(MessageFormat.format(DEFAULT_NAME_FORMAT, threadName));
        return builder.build();
    }

    public static ThreadFactory build(String threadName, boolean daemon) {
        ThreadFactoryBuilder builder = create(daemon);
        builder.setNameFormat(MessageFormat.format(DEFAULT_NAME_FORMAT, threadName));
        return builder.build();
    }
}
