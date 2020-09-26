package cn.ly.service.base_framework.common.filter;

import cn.ly.base_common.support.misc.Protocols;
import cn.ly.service.base_framework.common.consts.MetricsConst;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * Created by liaomengge on 16/11/9.
 */
public class MetricsFilter extends AbstractFilter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        URL url = invoker.getUrl();
        String protocol = url.getProtocol();

        long start = System.currentTimeMillis();
        String prefix = super.getMetricsPrefixName() + "." + invocation.getMethodName();

        boolean isSuccess = true;
        try {
            return invoker.invoke(invocation);
        } catch (RuntimeException e) {
            isSuccess = false;
            throw e;
        } finally {
            protocol = protocol.toLowerCase();
            if (!isExistProtocol(protocol)) {
                throw new UnsupportedOperationException("不支持该协议[" + protocol + "]操作");
            }
            if (isSuccess) {
                statsDClient.increment(prefix + "." + protocol + MetricsConst.REQ_EXE_SUC);
            } else {
                statsDClient.increment(prefix + "." + protocol + MetricsConst.REQ_EXE_FAIL);
            }
            long end = System.currentTimeMillis();
            statsDClient.recordExecutionTime(prefix + "." + protocol + MetricsConst.REQ_EXE_TIME, (end - start), 1);
        }
    }

    private boolean isExistProtocol(String protocol) {
        return Protocols.REST.equals(protocol) || Protocols.DUBBO.equals(protocol);
    }

}
