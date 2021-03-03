package com.github.liaomengge.base_common.ribbon.ribbon.predicate;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.github.liaomengge.base_common.nacos.consts.NacosConst;
import com.github.liaomengge.base_common.support.spring.SpringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by liaomengge on 2021/3/3.
 */
public class MetaDataTagPredicate extends NacosDiscoveryPredicate {

    @Override
    public boolean apply(NacosServer nacosServer) {
        NacosDiscoveryProperties nacosDiscoveryProperties = SpringUtils.getBean(NacosDiscoveryProperties.class);
        String clientTag = nacosDiscoveryProperties.getMetadata().get(NacosConst.MetadataConst.TAG);
        String serverTag = nacosServer.getMetadata().get(NacosConst.MetadataConst.TAG);
        if (StringUtils.isBlank(clientTag)) {
            return true;
        }
        if (StringUtils.isBlank(serverTag)) {
            return false;
        }
        return StringUtils.equalsIgnoreCase(clientTag, serverTag);
    }
}
