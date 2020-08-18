package cn.ly.base_common.helper.metric;

import cn.ly.base_common.utils.log4j2.LyLogger;
import com.timgroup.statsd.StatsDClient;
import lombok.Setter;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 16/11/10.
 */
public abstract class AbstractMetricMonitor {

    protected final Logger log = LyLogger.getInstance(AbstractMetricMonitor.class);

    @Setter
    protected StatsDClient statsDClient;

    @Setter
    protected String prefix = "";

    @Setter
    protected String suffix = "";

    public abstract void execute();
}
