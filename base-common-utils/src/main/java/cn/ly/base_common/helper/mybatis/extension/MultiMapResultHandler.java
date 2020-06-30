package cn.ly.base_common.helper.mybatis.extension;

import org.apache.ibatis.session.ResultContext;

import java.util.List;
import java.util.Map;

public class MultiMapResultHandler<T extends Map<?, List<?>>, K, V> extends MapResultHandler<T, K, V> {

    public MultiMapResultHandler(T mappedResults) {
        super(mappedResults);
    }

    @Override
    public void handleResult(ResultContext<? extends T> resultContext) {
        final Map<?, List<?>> retMap = resultContext.getResultObject();
        List<?> subRetList = retMap.get("value");
        super.getMappedResults().put((K) retMap.get("key"), (V) subRetList);
    }
}