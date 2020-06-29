package cn.mwee.base_common.mail;

import cn.mwee.base_common.helper.mail.MailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;

/**
 * Created by liaomengge on 2018/10/27.
 */
@Configuration
@ConditionalOnClass(MailSender.class)
@EnableConfigurationProperties(MailProperties.class)
public class MailAutoConfiguration {

    @Autowired
    private MailProperties mailProperties;

    @RefreshScope
    @Bean
    @ConditionalOnMissingBean
    public MailHelper mailHelper() {
        return new MailHelper(mailProperties.getSmtpHost(), mailProperties.getSmtpPort(),
                mailProperties.getUsername(), mailProperties.getPassword(), mailProperties.getNickname(),
                mailProperties.getTo(), mailProperties.getCc(), mailProperties.getBcc());
    }

}
