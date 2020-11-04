package com.github.liaomengge.base_common.helper.rest.async;

import com.github.liaomengge.base_common.helper.rest.Template;
import com.github.liaomengge.base_common.helper.rest.data.BaseRequest;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * Created by liaomengge on 17/3/9.
 */
@NoArgsConstructor
@Deprecated
public class AsyncClientTemplate extends Template.Async {

    @Setter
    private AsyncRestTemplate asyncRestTemplate;

    public AsyncClientTemplate(AsyncRestTemplate asyncRestTemplate) {
        this.asyncRestTemplate = asyncRestTemplate;
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(BaseRequest<?> baseRequest, Class<T> responseType) {
        String url = baseRequest.getUrl();
        HttpEntity httpEntity = new HttpEntity(baseRequest.getData());
        ListenableFuture<ResponseEntity<T>> listenableFuture = null;
        try {
            listenableFuture = asyncRestTemplate.postForEntity(url, httpEntity, responseType);
        } catch (Throwable e) {
            log.error("call service fail, request params[{}], url[{}], exception reason ===> [{}]",
                    LyJsonUtil.toJson4Log(baseRequest.getData()), url, e.getMessage());
        }
        return listenableFuture;
    }
}
