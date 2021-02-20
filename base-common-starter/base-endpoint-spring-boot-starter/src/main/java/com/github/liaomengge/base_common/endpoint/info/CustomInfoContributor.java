package com.github.liaomengge.base_common.endpoint.info;

import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.LinkedHashMap;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.JOINER;

/**
 * Created by liaomengge on 2019/7/4.
 */
public class CustomInfoContributor implements InfoContributor, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void contribute(Info.Builder builder) {
        String springApplicationName = this.environment.getProperty("spring.application.name");
        String[] springActiveEnv = environment.getActiveProfiles();
        if (ArrayUtils.isEmpty(springActiveEnv)) {
            springActiveEnv = environment.getDefaultProfiles();
        }
        String springAddress = LyNetworkUtil.getIpAddress();

        LinkedHashMap<String, String> infoHashMap = Maps.newLinkedHashMap();
        infoHashMap.put("name", springApplicationName);
        infoHashMap.put("env", JOINER.join(springActiveEnv));
        infoHashMap.put("address", springAddress);
        builder.withDetail("info", infoHashMap);
    }
}
