package com.github.liaomengge.base_common.helper.rest.sync.keepalive;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

/**
 * Created by liaomengge on 2021/12/16
 */
@NoArgsConstructor
@AllArgsConstructor
public class HttpConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

    private int keepAliveTime = 5000;//默认保活5s

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        Args.notNull(response, "HTTP response");
        HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                    return Long.parseLong(value) * 1000;
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return keepAliveTime;
    }

}
