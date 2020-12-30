package com.github.liaomengge.base_common.ribbon.loadbalance.metadata;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.github.liaomengge.base_common.nacos.consts.NacosConst;
import com.github.liaomengge.base_common.support.spring.SpringUtils;
import com.google.common.base.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Created by liaomengge on 2020/12/30.
 */
public class MetaDataTagPredicate implements Predicate<Instance> {

    @Override
    public boolean apply(@Nullable Instance input) {
        NacosDiscoveryProperties nacosDiscoveryProperties = SpringUtils.getBean(NacosDiscoveryProperties.class);
        String clientTag = nacosDiscoveryProperties.getMetadata().get(NacosConst.MetadataConst.TAG);
        String serverTag = input.getMetadata().get(NacosConst.MetadataConst.TAG);
        if (StringUtils.isBlank(clientTag)) {
            return true;
        }
        if (StringUtils.isBlank(serverTag)) {
            return false;
        }
        return StringUtils.endsWithIgnoreCase(clientTag, serverTag);
    }
}
