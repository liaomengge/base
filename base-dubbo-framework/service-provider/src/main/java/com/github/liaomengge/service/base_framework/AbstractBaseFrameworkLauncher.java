package com.github.liaomengge.service.base_framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liaomengge on 16/9/14.
 */
public abstract class AbstractBaseFrameworkLauncher {

    protected static final Logger log = LoggerFactory.getLogger(AbstractBaseFrameworkLauncher.class);
    protected static final byte[] lifeCycleLock = new byte[0];

}
