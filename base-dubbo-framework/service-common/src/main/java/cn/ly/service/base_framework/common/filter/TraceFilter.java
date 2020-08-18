package cn.ly.service.base_framework.common.filter;

import cn.ly.base_common.utils.trace.LyTraceLogUtil;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

import static cn.ly.base_common.utils.trace.LyTraceLogUtil.generateDefaultTraceLogIdPrefix;
import static cn.ly.base_common.utils.trace.LyTraceLogUtil.generateRandomSed;

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
