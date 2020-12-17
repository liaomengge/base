package com.github.liaomengge.base_common.feign.helper;

import com.github.liaomengge.base_common.feign.pojo.FeignTarget;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by liaomengge on 2020/12/11.
 */
public class FeignHelper implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public FeignTarget buildFeignTarget(FeignClient feignClient) {
        String name = getName(feignClient);
        String path = getPath(feignClient);
        String contextId = getContextId(feignClient);
        String url = getUrl(feignClient);
        String targetUrl = url;
        if (StringUtils.isBlank(targetUrl)) {
            if (!name.startsWith("http")) {
                targetUrl = "http://" + name;
            } else {
                targetUrl = name;
            }
            targetUrl += cleanPath(path);
            return FeignTarget.builder().name(name).path(path).contextId(contextId).url(url).targetUrl(targetUrl).build();
        }
        if (StringUtils.isNoneBlank(targetUrl) && !targetUrl.startsWith("http")) {
            targetUrl = "http://" + targetUrl;
        }
        targetUrl += cleanPath(path);
        return FeignTarget.builder().name(name).path(path).contextId(contextId).url(url).targetUrl(targetUrl).build();
    }

    private String getName(FeignClient feignClient) {
        String name = feignClient.serviceId();
        if (StringUtils.isBlank(name)) {
            name = feignClient.name();
        }
        if (StringUtils.isBlank(name)) {
            name = feignClient.value();
        }
        name = resolve(name);
        return FeignFeignTargetConvert.getName(name);
    }

    private String getUrl(FeignClient feignClient) {
        String url = resolve(feignClient.url());
        return FeignFeignTargetConvert.getUrl(url);
    }

    private String getPath(FeignClient feignClient) {
        String path = resolve(feignClient.path());
        return FeignFeignTargetConvert.getPath(path);
    }


    private String getContextId(FeignClient feignClient) {
        String contextId = feignClient.contextId();
        if (StringUtils.isBlank(contextId)) {
            return getName(feignClient);
        }

        contextId = resolve(contextId);
        return FeignFeignTargetConvert.getName(contextId);
    }

    private String cleanPath(String path) {
        String retPath = path.trim();
        if (StringUtils.isNoneBlank(retPath)) {
            if (!retPath.startsWith("/")) {
                retPath = "/" + retPath;
            }
            if (retPath.endsWith("/")) {
                retPath = retPath.substring(0, retPath.length() - 1);
            }
        }
        return retPath;
    }

    private String resolve(String value) {
        if (StringUtils.isNoneBlank(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    static class FeignFeignTargetConvert {

        static String getName(String name) {
            if (StringUtils.isBlank(name)) {
                return "";
            }

            String host = null;
            try {
                String url;
                if (!name.startsWith("http://") && !name.startsWith("https://")) {
                    url = "http://" + name;
                } else {
                    url = name;
                }
                host = new URI(url).getHost();

            } catch (URISyntaxException e) {
            }
            Assert.state(host != null, "Service id not legal hostname (" + name + ")");
            return name;
        }

        static String getUrl(String url) {
            if (StringUtils.isNoneBlank(url) && !(url.startsWith("#{") && url.contains("}"))) {
                if (!url.contains("://")) {
                    url = "http://" + url;
                }
                try {
                    new URL(url);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(url + " is malformed", e);
                }
            }
            return url;
        }

        static String getPath(String path) {
            if (StringUtils.isNoneBlank(path)) {
                path = path.trim();
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
            }
            return path;
        }
    }

}
