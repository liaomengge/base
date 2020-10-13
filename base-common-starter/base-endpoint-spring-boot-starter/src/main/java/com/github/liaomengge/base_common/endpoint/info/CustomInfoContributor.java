package com.github.liaomengge.base_common.endpoint.info;

import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import com.google.common.collect.Maps;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.LinkedHashMap;

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
        String springActiveEnv = this.environment.getProperty("spring.profiles.active");
        String address = LyNetworkUtil.getHostAddress();
        LinkedHashMap<String, String> infoHashMap = Maps.newLinkedHashMap();
        infoHashMap.put("name", springApplicationName);
        infoHashMap.put("env", springActiveEnv);
        infoHashMap.put("address", address);
        infoHashMap.put("time", LyJdk8DateUtil.getNowDate2String());
        builder.withDetail("info", infoHashMap);
    }
}
