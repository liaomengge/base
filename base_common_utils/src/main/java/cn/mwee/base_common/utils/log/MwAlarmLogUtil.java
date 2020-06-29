package cn.mwee.base_common.utils.log;

import cn.mwee.base_common.utils.log4j2.MwLogger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 18/4/20.
 */
public final class MwAlarmLogUtil {

    private static final Logger logger = MwLogger.getInstance(MwAlarmLogUtil.class);

    private interface AlarmEnum {

        String alarmName();

        default void warn() {
            logger.warn("[" + this.alarmName() + "] !!!");
        }

        default void warn(String msg) {
            logger.warn("[" + this.alarmName() + "] ===> " + msg);
        }

        default void warn(String format, Object arg) {
            logger.warn("[" + this.alarmName() + "] ===> " + format, arg);
        }

        default void warn(String format, Object arg1, Object arg2) {
            logger.warn("[" + this.alarmName() + "] ===> " + format, arg1, arg2);
        }

        default void warn(String format, Object... arguments) {
            logger.warn("[" + this.alarmName() + "] ===> " + format, arguments);
        }

        default void warn(Throwable t) {
            logger.warn("[" + this.alarmName() + "] ===> ", t);
        }

        default void warn(String msg, Throwable t) {
            logger.warn("[" + this.alarmName() + "] ===> " + msg, t);
        }

        default void error() {
            logger.error("[" + this.alarmName() + "] !!!");
        }

        default void error(String msg) {
            logger.error("[" + this.alarmName() + "] ===> " + msg);
        }

        default void error(String format, Object arg) {
            logger.error("[" + this.alarmName() + "] ===> " + format, arg);
        }

        default void error(String format, Object arg1, Object arg2) {
            logger.error("[" + this.alarmName() + "] ===> " + format, arg1, arg2);
        }

        default void error(String format, Object... arguments) {
            logger.error("[" + this.alarmName() + "] ===> " + format, arguments);
        }

        default void error(Throwable t) {
            logger.error("[" + this.alarmName() + "] ===> ", t);
        }

        default void error(String msg, Throwable t) {
            logger.error("[" + this.alarmName() + "] ===> " + msg, t);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum MiddlewareEnum implements AlarmEnum {
        BASE_PREFIX_DB("数据库异常") {
            @Override
            public String alarmName() {
                return this.name() + '(' + this.getDesc() + ')';
            }
        },
        BASE_PREFIX_REDIS("Redis缓存异常") {
            @Override
            public String alarmName() {
                return this.name() + '(' + this.getDesc() + ')';
            }
        },
        BASE_PREFIX_MQ("消息队列异常") {
            @Override
            public String alarmName() {
                return this.name() + '(' + this.getDesc() + ')';
            }
        };

        private String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum ServerProjEnum implements AlarmEnum {
        BASE_PREFIX_PARAM("参数异常") {
            @Override
            public String alarmName() {
                return this.name();
            }
        },
        BASE_PREFIX_SIGN("签名失败") {
            @Override
            public String alarmName() {
                return this.name();
            }
        },
        BASE_PREFIX_BIZ("业务异常") {
            @Override
            public String alarmName() {
                return this.name() + '(' + this.getDesc() + ')';
            }
        },
        BASE_PREFIX_UNKNOWN("服务器内部异常") {
            @Override
            public String alarmName() {
                return this.name() + '(' + this.getDesc() + ')';
            }
        };

        private String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum ClientProjEnum implements AlarmEnum {
        BASE_PREFIX_CALLER_BIZ("第三方业务异常") {
            @Override
            public String alarmName() {
                return this.name() + '(' + this.getDesc() + ')';
            }
        },
        BASE_PREFIX_CALLER_HTTP("第三方网络异常") {
            @Override
            public String alarmName() {
                return this.name() + '(' + this.getDesc() + ')';
            }
        };

        private String desc;
    }
}
