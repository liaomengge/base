package com.github.liaomengge.base_common.utils.net;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.Enumeration;
import java.util.Objects;

/**
 * Created by liaomengge on 17/10/10.
 */
@Slf4j
@UtilityClass
public class LyNetworkUtil {
    
    /**
     * 获取请求主机IP地址,如果通过代理进来, 则透过防火墙获取真实IP地址;
     * X-Forwarded-For：Squid 服务代理
     * Proxy-Client-IP：apache 服务代理
     * WL-Proxy-Client-IP：weblogic 服务代理
     * HTTP_CLIENT_IP：有些代理服务器
     * X-Real-IP：nginx服务代理
     *
     * @param request
     * @return
     */
    public final String getRemoteIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 获取主机IP地址
     *
     * @return
     */
    public String getIpAddress() {
        String result = "127.0.0.1";
        InetAddress inetAddress = findFirstNonLoopbackAddress();
        if (Objects.nonNull(inetAddress)) {
            result = StringUtils.defaultIfBlank(inetAddress.getHostAddress(), result);
        }
        return result;
    }

    /**
     * 获取主机计算机名
     *
     * @return
     */
    public String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return getLocalHostName();
        }
    }

    private String getLocalHostName() {
        String result = "localhost";
        InetAddress inetAddress = findFirstNonLoopbackAddress();
        if (Objects.nonNull(inetAddress)) {
            result = StringUtils.defaultIfBlank(inetAddress.getHostName(), result);
        }
        return result;
    }

    /**
     * 参考
     * {@link org.springframework.cloud.commons.util.InetUtils#findFirstNonLoopbackAddress()}
     *
     * @return
     */
    private InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;
        try {
            int lowest = Integer.MAX_VALUE;
            for (Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
                 networkInterfaceEnumeration.hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                if (networkInterface.isUp()) {
                    if (networkInterface.getIndex() < lowest || result == null) {
                        lowest = networkInterface.getIndex();
                    } else if (result != null) {
                        continue;
                    }

                    for (Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
                         inetAddressEnumeration.hasMoreElements(); ) {
                        InetAddress inetAddress = inetAddressEnumeration.nextElement();
                        if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                            result = inetAddress;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            log.error("cannot get first non-loopback address", e);
        }

        if (result != null) {
            return result;
        }

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.warn("unable to retrieve localhost");
        }
        return null;
    }

}
