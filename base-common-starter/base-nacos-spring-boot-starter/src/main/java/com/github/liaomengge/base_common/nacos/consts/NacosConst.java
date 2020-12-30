package com.github.liaomengge.base_common.nacos.consts;

/**
 * Created by liaomengge on 2020/8/15.
 */
public interface NacosConst {

    interface MetadataConst {
        String SPRING_BOOT_VERSION = "spring.boot.version";
        String SPRING_APPLICATION_NAME = "spring.application.name";

        String APPLICATION_CONTEXT_PATH = "server.servlet.context-path";
        String APPLICATION_SERVER_PORT = "server.port";

        String PRESERVED_REGISTER_TIME = "preserved.register.time";

        String TAG = "tag";
    }

    interface EndpointConst {
        String PULL_IN = "pullin";
        String PULL_OUT = "pullout";
    }
}
