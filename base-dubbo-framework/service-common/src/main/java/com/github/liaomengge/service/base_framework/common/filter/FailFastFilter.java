package com.github.liaomengge.service.base_framework.common.filter;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.*;
import com.github.liaomengge.base_common.support.meter._MeterRegistrys;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.github.liaomengge.service.base_framework.base.DataResult;
import com.github.liaomengge.service.base_framework.common.consts.MetricsConst;
import com.github.liaomengge.service.base_framework.common.consts.ServiceConst;
import com.github.liaomengge.service.base_framework.common.pojo.FilterLogInfo;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.Counter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by liaomengge on 2016/9/19.
 */
public class FailFastFilter extends AbstractFilter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String failFastMethodName = filterConfig.getFailFastMethodName();
        if (StringUtils.isBlank(failFastMethodName)) {
            return invoker.invoke(invocation);
        }
        String methodName = invocation.getMethodName();
        Iterable<String> iterable =
                Splitter.on(",").omitEmptyStrings().trimResults().omitEmptyStrings().split(failFastMethodName);
        if (!Iterables.contains(iterable, methodName)) {
            return invoker.invoke(invocation);
        }
        FilterLogInfo logInfo = new FilterLogInfo();
        logInfo.setInvocation(invocation.toString());

        RpcContext rpcContext = RpcContext.getContext();
        logInfo.setRemoteIp(rpcContext.getRemoteAddressString());
        logInfo.setHostIp(rpcContext.getLocalAddressString());

        Map<String, Object> rpcResponseMap = Maps.newHashMap();
        rpcResponseMap.put("code", ServiceConst.ResponseStatus.ErrorCodeEnum.FAIL_FAST_ERROR.getCode());
        rpcResponseMap.put("msg", ServiceConst.ResponseStatus.ErrorCodeEnum.FAIL_FAST_ERROR.getMsg());

        DataResult<Map<String, Object>> dataResult = new DataResult<>(rpcResponseMap);
        RpcResult result = new RpcResult(dataResult);

        URL url = invoker.getUrl();
        String protocol = url.getProtocol();
        String prefix = super.getMetricsPrefixName() + "." + methodName;
        _MeterRegistrys.counter(meterRegistry, prefix + MetricsConst.FAIL_FAST_EXE_FAIL, PROTOCOL_TAG, protocol).ifPresent(Counter::increment);

        logInfo.setResult(result.getValue());
        logInfo.setRestUrl(url.getAbsolutePath());
        log.info(LyJsonUtil.toJson4Log(logInfo));

        return result;
    }
}
