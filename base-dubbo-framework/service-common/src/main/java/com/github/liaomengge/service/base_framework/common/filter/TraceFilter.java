package com.github.liaomengge.service.base_framework.common.filter;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;

import static com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil.generateDefaultTraceLogIdPrefix;
import static com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil.generateRandomSed;

/**
 * Created by liaomengge on 16/7/11.
 */
public class TraceFilter extends AbstractFilter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String methodName = invocation.getMethodName();
        if (SKIP_METHOD.equalsIgnoreCase(methodName)) {
            return invoker.invoke(invocation);
        }

        String traceId = generateRandomSed(generateDefaultTraceLogIdPrefix());
        LyTraceLogUtil.put(traceId);

        return invoker.invoke(invocation);
    }
}
