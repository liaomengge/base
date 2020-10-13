package com.github.liaomengge.base_common.quartz.lock.initializer;

import com.github.liaomengge.base_common.helper.mail.MailHelper;
import com.github.liaomengge.base_common.helper.zk.AbstractLock;
import com.github.liaomengge.base_common.quartz.lock.QuartzLockProperties;
import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import com.github.liaomengge.base_common.utils.shutdown.LyShutdownUtil;
import org.I0Itec.zkclient.ZkClient;
import org.quartz.SchedulerException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by liaomengge on 2019/5/21.
 */
public class QuartzLockInitializer extends AbstractLock implements EnvironmentAware, ApplicationContextAware,
        InitializingBean {

    private static final String SPRING_APPLICATION_NAME = "spring.application.name";

    @Autowired
    private QuartzLockProperties quartzLockProperties;

    private Environment environment;
    private ApplicationContext applicationContext;

    private SchedulerFactoryBean schedulerFactoryBean;

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
        if (Objects.nonNull(schedulerFactoryBean) && !schedulerFactoryBean.isRunning()) {
            schedulerFactoryBean.start();
            log.info("Machine[{}], quartz acquire lock success, start up...", LyNetworkUtil.getHostAddress());
        }
        this.registerShutdownHook(schedulerFactoryBean);
    }

    @Override
    protected void lockFail() {
        if (Objects.nonNull(schedulerFactoryBean) && schedulerFactoryBean.isRunning()) {
            schedulerFactoryBean.stop();
            log.info("Machine[{}], quartz acquire lock fail, start fail...", LyNetworkUtil.getHostAddress());
        }
    }

    @Override
    protected void initBean() {
        String applicationName = environment.getProperty(SPRING_APPLICATION_NAME, this.getClass().getSimpleName());
        try {
            setZkClient(applicationContext.getBean(ZkClient.class));
            schedulerFactoryBean = applicationContext.getBean(SchedulerFactoryBean.class);
            schedulerFactoryBean.stop();
            MailHelper mailHelper = applicationContext.getBean(MailHelper.class);
            Optional.ofNullable(mailHelper).ifPresent(val -> val.sendTextMail(applicationName,
                    applicationName + " quartz start up..."));
        } catch (Exception e) {
            log.error(applicationName + " quartz start exception", e);
        }
    }

    /**
     * 程序退出时的回调勾子
     *
     * @param schedulerFactoryBean
     */
    private void registerShutdownHook(SchedulerFactoryBean schedulerFactoryBean) {
        LyShutdownUtil.registerShutdownHook(() -> {
            try {
                log.info("Quartz Scheduler FactoryBean Exist...");
            } finally {
                if (schedulerFactoryBean != null) {
                    try {
                        schedulerFactoryBean.destroy();
                    } catch (SchedulerException e) {
                        log.error("Destroy Quartz Scheduler Exception", e);
                    }
                }
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        getLockBoot(quartzLockProperties.getLockNumber(), quartzLockProperties.getRootNode());
    }
}
