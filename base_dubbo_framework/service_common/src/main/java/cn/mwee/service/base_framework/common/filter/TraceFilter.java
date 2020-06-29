package cn.mwee.service.base_framework.common.filter;

import cn.mwee.base_common.utils.trace.MwTraceLogUtil;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

import static cn.mwee.base_common.utils.trace.MwTraceLogUtil.generateDefaultTraceLogIdPrefix;
import static cn.mwee.base_common.utils.trace.MwTraceLogUtil.generateRandomSed;

/**
 * Created by liaomengge on 17/7/11.
 */
public class TraceFilter extends AbstractFilter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String methodName = invocation.getMethodName();
        if (SKIP_METHOD.equalsIgnoreCase(methodName)) {
            return invoker.invoke(invocation);
        }

        String traceId = generateRandomSed(generateDefaultTraceLogIdPrefix());
        MwTraceLogUtil.put(traceId);

        return invoker.invoke(invocation);
    }
}
