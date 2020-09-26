package cn.ly.base_common.convert;

import com.google.common.base.Charsets;

import java.nio.charset.Charset;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by liaomengge on 2018/12/10.
 */
@Data
@ConfigurationProperties("ly.http.encoding")
public class FastJsonConvertProperties {

    private Charset charset = Charsets.UTF_8;
}
