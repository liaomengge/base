package cn.ly.service.base_framework.common.filter;

import cn.ly.base_common.utils.log4j2.LyLogData;
import cn.ly.service.base_framework.base.DataResult;
import cn.ly.service.base_framework.common.consts.MetricsConst;
import cn.ly.service.base_framework.common.consts.ServiceConst.ResponseStatus.ErrorCodeEnum;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
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
        Iterable<String> iterable = Splitter.on(",").omitEmptyStrings().trimResults().omitEmptyStrings().split(failFastMethodName);
        if (!Iterables.contains(iterable, methodName)) {
            return invoker.invoke(invocation);
        }
        LyLogData logData = new LyLogData();
        logData.setInvocation(invocation.toString());

        RpcContext rpcContext = RpcContext.getContext();
        logData.setRemoteIp(rpcContext.getRemoteAddressString());
        logData.setHostIp(rpcContext.getLocalAddressString());

        Map<String, Object> rpcResponseMap = Maps.newHashMap();
        rpcResponseMap.put("code", ErrorCodeEnum.FAIL_FAST_ERROR.getCode());
        rpcResponseMap.put("msg", ErrorCodeEnum.FAIL_FAST_ERROR.getMsg());

        DataResult<Map<String, Object>> dataResult = new DataResult<>(rpcResponseMap);
        RpcResult result = new RpcResult(dataResult);

        URL url = invoker.getUrl();
        String protocol = url.getProtocol();
        String prefix = super.getMetricsPrefixName() + "." + methodName;
        statsDClient.increment(prefix + "." + protocol + MetricsConst.FAIL_FAST_EXE_FAIL);

        logData.setResult(result.getValue());
        logData.setRestUrl(url.getAbsolutePath());
        logger.info(logData);

        return result;
    }
}
