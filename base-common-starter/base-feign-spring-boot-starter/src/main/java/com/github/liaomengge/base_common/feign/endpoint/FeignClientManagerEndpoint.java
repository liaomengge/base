package com.github.liaomengge.base_common.feign.endpoint;

import com.github.liaomengge.base_common.feign.consts.FeignConst;
import com.github.liaomengge.base_common.feign.manager.FeignClientManager;
import com.github.liaomengge.base_common.feign.pojo.FeignTarget;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Map;

/**
 * Created by liaomengge on 2021/6/2.
 */
@AllArgsConstructor
@Endpoint(id = FeignConst.EndpointConst.FEIGN_CLIENTS)
public class FeignClientManagerEndpoint {

    private final FeignClientManager feignClientManager;

    @ReadOperation
    public Map<String, FeignTarget> feign() {
        return feignClientManager.getFeignTargetMap();
    }
}
