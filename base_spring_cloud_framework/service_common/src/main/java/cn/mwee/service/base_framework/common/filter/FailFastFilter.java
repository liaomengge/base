package cn.mwee.service.base_framework.common.filter;

import cn.mwee.service.base_framework.base.DataResult;
import cn.mwee.service.base_framework.base.StringRestResponse;
import cn.mwee.service.base_framework.common.config.FilterConfig;
import cn.mwee.service.base_framework.common.filter.chain.FilterChain;
import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.annotation.Order;

import static cn.mwee.base_common.support.misc.consts.ToolConst.SPLITTER;
import static cn.mwee.service.base_framework.base.code.SystemResultCode.FAIL_FAST_ERROR;

/**
 * Created by liaomengge on 2018/11/29.
 */
@Order(500)
@AllArgsConstructor
public class FailFastFilter extends AbstractFilter {

    private final FilterConfig filterConfig;

    @Override
    public Object doFilter(ProceedingJoinPoint joinPoint, FilterChain chain) throws Throwable {
        String failFastMethodName = filterConfig.getFailFast().getMethodName();
        if (StringUtils.isBlank(failFastMethodName)) {
            return chain.doFilter(joinPoint, chain);
        }
        String methodName = super.getMethodName(joinPoint);
        Iterable<String> iterable = SPLITTER.split(failFastMethodName);
        if (!Iterables.contains(iterable, methodName)) {
            return chain.doFilter(joinPoint, chain);
        }
        DataResult<StringRestResponse> dataResult = new DataResult<>(false);

        StringRestResponse restResponse = new StringRestResponse();
        restResponse.setErrNo(FAIL_FAST_ERROR.getCode());
        restResponse.setErrMsg(FAIL_FAST_ERROR.getDescription());
        dataResult.setData(restResponse);
        return dataResult;
    }
}
