package com.github.liaomengge.base_common.mq.initializer;

import com.github.liaomengge.base_common.helper.mail.MailHelper;
import com.github.liaomengge.base_common.helper.zk.AbstractLock;
import com.github.liaomengge.base_common.mq.MQLockProperties;
import com.github.liaomengge.base_common.mq.rabbitmq.receiver.BaseMQReceiver;
import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by liaomengge on 2019/5/21.
 */
public class MQLockInitializer extends AbstractLock implements EnvironmentAware, ApplicationContextAware,
        InitializingBean {

    private static final String SPRING_APPLICATION_NAME = "spring.application.name";
    private static final String MQ_TYPE = "base.mq.type";
    private static final String RABBITMQ = "rabbitmq";
    private static final String ACTIVEMQ = "activemq";

    @Autowired
    private MQLockProperties mqLockProperties;

    private Environment environment;
    private ApplicationContext applicationContext;

    private Map<String, com.github.liaomengge.base_common.mq.activemq.receiver.BaseMQReceiver> baseActiveMQReceiverMap;
    private Map<String, BaseMQReceiver> baseRabbitMQReceiverMap;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void lockSuccess() {
        Optional.ofNullable(baseActiveMQReceiverMap).ifPresent(val -> val.forEach((key, value) -> {
            if (isSingleton(key)) {
                value.start();
                log.info("Ip[{}], Singleton ActiveMQ[{}] acquire lock success, start up...",
                        LyNetworkUtil.getHostAddress(), key);
            }
        }));
        Optional.ofNullable(baseRabbitMQReceiverMap).ifPresent(val -> val.forEach((key, value) -> {
            if (isSingleton(key)) {
                value.start();
                log.info("Ip[{}], Singleton RabbitMQ[{}] acquire lock success, start up...",
                        LyNetworkUtil.getHostAddress(), key);
            }
        }));
    }

    @Override
    protected void lockFail() {
        Optional.ofNullable(baseActiveMQReceiverMap).ifPresent(val -> val.forEach((key, value) -> {
            if (isSingleton(key)) {
                value.stop();
                log.info("Ip[{}], Singleton ActiveMQ[{}] acquire lock fail, start fail...",
                        LyNetworkUtil.getHostAddress(), key);
            }
        }));
        Optional.ofNullable(baseRabbitMQReceiverMap).ifPresent(val -> val.forEach((key, value) -> {
            if (isSingleton(key)) {
                value.stop();
                log.info("Ip[{}], Singleton RabbitMQ[{}] acquire lock fail, start fail...",
                        LyNetworkUtil.getHostAddress(), key);
            }
        }));
    }

    @Override
    protected void initBean() {
        MQLockProperties.PrototypeProperties prototypeProperties = mqLockProperties.getPrototype();
        List<String> beanNames = prototypeProperties.getBeanNames();
        String applicationName = environment.getProperty(SPRING_APPLICATION_NAME, this.getClass().getSimpleName());
        String mqType = environment.getProperty(MQ_TYPE);
        try {
            setZkClient(applicationContext.getBean(ZkClient.class));
            if (StringUtils.equalsIgnoreCase(RABBITMQ, mqType)) {
                baseRabbitMQReceiverMap = applicationContext.getBeansOfType(BaseMQReceiver.class);
                Optional.ofNullable(baseRabbitMQReceiverMap).ifPresent(val -> val.forEach((key, value) -> value.stop()));
                Optional.ofNullable(beanNames).ifPresent(val -> val.stream().forEach(val2 -> {
                    BaseMQReceiver baseMQReceiver = baseRabbitMQReceiverMap.get(val2);
                    Optional.ofNullable(baseMQReceiver).ifPresent(val3 -> {
                        val3.start();
                        log.info("Ip[{}], Prototype ActiveMQ[{}], start up...", LyNetworkUtil.getHostAddress(),
                                val2);
                    });
                }));
            } else if (StringUtils.equalsIgnoreCase(ACTIVEMQ, mqType)) {
                baseActiveMQReceiverMap =
                        applicationContext.getBeansOfType(com.github.liaomengge.base_common.mq.activemq.receiver.BaseMQReceiver.class);
                Optional.ofNullable(baseActiveMQReceiverMap).ifPresent(val -> val.forEach((key, value) -> value.stop()));
                Optional.ofNullable(beanNames).ifPresent(val -> val.stream().forEach(val2 -> {
                    com.github.liaomengge.base_common.mq.activemq.receiver.BaseMQReceiver baseMQReceiver =
                            baseActiveMQReceiverMap.get(val2);
                    Optional.ofNullable(baseMQReceiver).ifPresent(val3 -> {
                        val3.start();
                        log.info("Ip[{}], Prototype RabbitMQ[{}], start up...", LyNetworkUtil.getHostAddress()
                                , val2);
                    });
                }));
            }
            MailHelper mailHelper = applicationContext.getBean(MailHelper.class);
            Optional.ofNullable(mailHelper).ifPresent(val -> val.sendTextMail(applicationName,
                    applicationName + " mq start up..."));
        } catch (Exception e) {
            log.error(applicationName + " mq start exception", e);
        }
    }

    private boolean isSingleton(String beanName) {
        MQLockProperties.PrototypeProperties prototypeProperties = mqLockProperties.getPrototype();
        List<String> beanNames = prototypeProperties.getBeanNames();
        return CollectionUtils.isEmpty(beanNames) || !beanNames.contains(beanName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        getLockBoot(mqLockProperties.getLockNumber(), mqLockProperties.getRootNode());
    }
}
