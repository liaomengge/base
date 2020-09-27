package cn.ly.base_common.utils.spring;

import cn.ly.base_common.support.datasource.DBContext;
import cn.ly.base_common.support.datasource.enums.DbType;
import cn.ly.base_common.utils.log4j2.LyLogger;

import java.io.File;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * {type your description }
 *
 * @since: 15/11/16.
 * @author: liaomengge
 */
public class LyApplicationContextUtil {

    private static final Logger log = LyLogger.getInstance(LyApplicationContextUtil.class);

    private static ConfigurableApplicationContext context = null;
    private volatile static LyApplicationContextUtil instance = null;

    private String springConfigFile = "resources/spring-context.xml";
    private String log4j2ConfigFile = "resources/log4j2.xml";
    private DbType defaultDbType = DbType.MASTER;

    public static LyApplicationContextUtil getInstance() {
        if (instance == null) {
            synchronized (LyApplicationContextUtil.class) {
                if (instance == null) {
                    instance = new LyApplicationContextUtil();
                    instance.init();
                }
            }
        }
        return instance;
    }

    public static LyApplicationContextUtil getInstance(String springConfigFile) {
        if (instance == null) {
            synchronized (LyApplicationContextUtil.class) {
                if (instance == null) {
                    instance = new LyApplicationContextUtil(springConfigFile);
                    instance.init();
                }
            }
        }
        return instance;
    }

    public static LyApplicationContextUtil getInstance(String springConfigFile, String log4j2ConfigFile) {
        if (instance == null) {
            synchronized (LyApplicationContextUtil.class) {
                if (instance == null) {
                    instance = new LyApplicationContextUtil(springConfigFile, log4j2ConfigFile);
                    instance.init();
                }
            }
        }
        return instance;
    }

    public static LyApplicationContextUtil getInstance(String springConfigFile, DbType dbType) {
        if (instance == null) {
            synchronized (LyApplicationContextUtil.class) {
                if (instance == null) {
                    instance = new LyApplicationContextUtil(springConfigFile, dbType);
                    instance.init();
                }
            }
        }
        return instance;
    }

    public ConfigurableApplicationContext getContext() {
        return context;
    }

    public void close() {
        context.close();
    }

    private LyApplicationContextUtil() {
    }

    private LyApplicationContextUtil(String springConfigFile) {
        this.springConfigFile = springConfigFile;
    }

    private LyApplicationContextUtil(String springConfigFile, String log4j2ConfigFile) {
        this.springConfigFile = springConfigFile;
        this.log4j2ConfigFile = log4j2ConfigFile;
    }

    private LyApplicationContextUtil(String springConfigFile, DbType dbType) {
        this.springConfigFile = springConfigFile;
        this.defaultDbType = dbType;
    }

    private void init() {
        //加载log4j2.xml
        File configFile = new File(log4j2ConfigFile);
        if (!configFile.exists()) {
            log.error("log4j2 config file:" + configFile.getAbsolutePath() + " not exist");
            return;
        }
        log.info("log4j2 config file:" + configFile.getAbsolutePath());

        try {
            //注:这一句必须放在整个应用第一次LoggerFactory.getLogger(XXX.class)前执行
            System.setProperty("log4j.configurationFile", configFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("log4j2 initialize error:" + e.getLocalizedMessage());
            return;
        }

        //加载spring配置文件
        configFile = new File(springConfigFile);

        if (!configFile.exists()) {
            log.error("spring config file:" + configFile.getAbsolutePath() + " not exist");
            return;
        }

        if (context == null) {
            DBContext.setDBKey(defaultDbType);
            context = new FileSystemXmlApplicationContext(springConfigFile);

            //做HA时, 很多场景会启动一个standby实例(做为冗余), 仅当master宕机时, standby实例才会激活
            //在启动时, 先关闭所有conn, 释放这些不必要的连接资源
            String[] dsList = context.getBeanNamesForType(DataSource.class);
            for (String ds : dsList) {
                DataSource dataSource = context.getBean(ds, DataSource.class);
                if (dataSource != null) {
                    java.sql.Connection connection;
                    try {
                        connection = dataSource.getConnection();
                        connection.close();
                    } catch (SQLException e) {
                        System.err.println("spring initialize error:" + e.getLocalizedMessage());
                        throw new RuntimeException(e);
                    }
                }
            }

            String[] redisConnList = context.getBeanNamesForType(JedisConnectionFactory.class);
            for (String beanName : redisConnList) {
                JedisConnectionFactory connectionFactory = context.getBean(beanName, JedisConnectionFactory.class);
                if (connectionFactory != null) {
                    connectionFactory.getConnection().close();
                }
            }
        }

    }
}
