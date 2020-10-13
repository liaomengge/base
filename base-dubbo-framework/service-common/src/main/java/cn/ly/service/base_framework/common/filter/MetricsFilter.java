package cn.ly.service.base_framework.common.filter;

import cn.ly.base_common.support.misc.Protocols;
import cn.ly.service.base_framework.common.consts.MetricsConst;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

import java.time.Duration;
import java.util.Optional;

/**
 * Created by liaomengge on 16/11/9.
 */
public class MetricsFilter extends AbstractFilter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        URL url = invoker.getUrl();
        String protocol = url.getProtocol();

        long startTime = System.nanoTime();
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
            long endTime = System.nanoTime();
            this.statExec(prefix, protocol, isSuccess, (endTime - startTime));
        }
    }

    private boolean isExistProtocol(String protocol) {
        return Protocols.REST.equals(protocol) || Protocols.DUBBO.equals(protocol);
    }

    private void statExec(String prefix, String protocol, boolean isSuccess, long elapsedNanoTime) {
        Optional.ofNullable(meterRegistry).ifPresent(val -> {
            if (isSuccess) {
                val.counter(prefix + MetricsConst.REQ_EXE_SUC, PROTOCOL_TAG, protocol).increment();
                val.counter(MetricsConst.REQ_ALL + MetricsConst.REQ_EXE_SUC).increment();
            } else {
                val.counter(prefix + MetricsConst.REQ_EXE_FAIL, PROTOCOL_TAG, protocol).increment();
                val.counter(MetricsConst.REQ_ALL + MetricsConst.REQ_EXE_FAIL).increment();
            }
            val.timer(prefix + MetricsConst.REQ_EXE_TIME, PROTOCOL_TAG, protocol).record(Duration.ofNanos(elapsedNanoTime));
        });
    }

}
