package com.github.liaomengge.base_common.fastjson;

import com.google.common.base.Charsets;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.Charset;

/**
 * Created by liaomengge on 2018/12/10.
 */
@Data
@ConfigurationProperties("base.http.encoding")
public class FastJsonProperties {

    private Charset charset = Charsets.UTF_8;
}
