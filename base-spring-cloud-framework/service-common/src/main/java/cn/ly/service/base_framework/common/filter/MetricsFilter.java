package cn.ly.service.base_framework.common.filter;

import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import cn.ly.service.base_framework.common.consts.MetricsConst;
import cn.ly.service.base_framework.common.filter.chain.FilterChain;
import cn.ly.service.base_framework.common.util.TimeThreadLocalUtil;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.annotation.Order;

/**
 * Created by liaomengge on 2018/11/22.
 */
@Order(3000)
@AllArgsConstructor
public class MetricsFilter extends AbstractFilter {

    private final StatsDClient statsDClient;

    @Override
    public Object doFilter(ProceedingJoinPoint joinPoint, FilterChain chain) throws Throwable {
        boolean isSuccess = true;
        try {
            return chain.doFilter(joinPoint, chain);
        } catch (Exception e) {
            isSuccess = false;
            throw e;
        } finally {
            String prefix = super.getMethodName(joinPoint);
            if (isSuccess) {
                statsDClient.increment(prefix + MetricsConst.REQ_EXE_SUC);
                statsDClient.increment(MetricsConst.REQ_ALL + MetricsConst.REQ_EXE_SUC);
            } else {
                statsDClient.increment(prefix + MetricsConst.REQ_EXE_FAIL);
                statsDClient.increment(MetricsConst.REQ_ALL + MetricsConst.REQ_EXE_FAIL);
            }
            long elapsedMilliseconds = LyJdk8DateUtil.getMilliSecondsTime() - TimeThreadLocalUtil.get();
            statsDClient.recordExecutionTime(prefix + MetricsConst.REQ_EXE_TIME, elapsedMilliseconds, 1);
        }
    }
}
