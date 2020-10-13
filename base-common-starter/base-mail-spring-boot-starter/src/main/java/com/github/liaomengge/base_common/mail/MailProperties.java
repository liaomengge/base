package com.github.liaomengge.base_common.mail;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * Created by liaomengge on 2018/10/27.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "base.mail")
public class MailProperties {

    @NotNull
    private String smtpHost;
    private int smtpPort = 465;
    private String username;
    private String password;
    private String nickname;
    private String to;
    private String cc;
    private String bcc;
}
