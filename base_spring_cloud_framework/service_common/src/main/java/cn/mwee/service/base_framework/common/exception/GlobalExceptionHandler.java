package cn.mwee.service.base_framework.common.exception;

import cn.mwee.base_common.support.exception.AbstractAppException;
import cn.mwee.base_common.support.exception.AbstractAppRuntimeException;
import cn.mwee.base_common.support.exception.BusinessException;
import cn.mwee.base_common.utils.error.MwThrowableUtil;
import cn.mwee.base_common.utils.number.MwMoreNumberUtil;
import cn.mwee.service.base_framework.base.DataResult;
import cn.mwee.service.base_framework.base.code.SystemResultCode;
import org.redisson.client.RedisException;
import org.springframework.amqp.AmqpException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import redis.clients.jedis.exceptions.JedisException;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import static cn.mwee.base_common.utils.log.MwAlarmLogUtil.MiddlewareEnum.*;
import static cn.mwee.base_common.utils.log.MwAlarmLogUtil.ServerProjEnum.BASE_PREFIX_BIZ;
import static cn.mwee.base_common.utils.log.MwAlarmLogUtil.ServerProjEnum.BASE_PREFIX_UNKNOWN;
import static cn.mwee.service.base_framework.base.code.SystemResultCode.*;

/**
 * Created by liaomengge on 2018/10/23.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public DataResult defaultErrorHandler(Exception e) {
        DataResult dataResult = buildAlarm(e);
        if (e instanceof AbstractAppException) {
            dataResult.setSysErrCode(((AbstractAppException) e).getErrCode());
            dataResult.setSysErrDesc(((AbstractAppException) e).getErrMsg());
        }
        if (e instanceof AbstractAppRuntimeException) {
            dataResult.setSysErrCode(((AbstractAppRuntimeException) e).getErrCode());
            dataResult.setSysErrDesc(((AbstractAppRuntimeException) e).getErrMsg());
        }

        dataResult.setSysException(MwThrowableUtil.getStackTrace(e));
        return dataResult;
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (Objects.isNull(ex) && Objects.isNull(status)) {
            return super.handleExceptionInternal(ex, DataResult.fail(UNKNOWN_ERROR), headers, status, request);
        }
        String statusCode = Optional.ofNullable(status).map(val -> MwMoreNumberUtil.toString(val.value())).orElse("");
        String statusDesc = Optional.ofNullable(status).map(val -> val.getReasonPhrase()).orElse("");
        String sysErrDesc = "HttpStatus[" + statusCode + "], Error Message[" + statusDesc + "]";
        String sysException = Optional.ofNullable(ex).map(val -> MwThrowableUtil.getStackTrace(val)).orElse("");
        DataResult dataResult = DataResult.fail(SystemResultCode.UNKNOWN_ERROR, sysErrDesc, sysException);
        return super.handleExceptionInternal(ex, dataResult, headers, status, request);
    }

    private DataResult buildAlarm(Exception e) {
        DataResult dataResult = DataResult.fail(SERVER_ERROR);
        if (e instanceof JedisException || e instanceof RedisException) {
            BASE_PREFIX_REDIS.error(e);
            return DataResult.fail(REDIS_ERROR);
        }
        if (e instanceof JmsException || e instanceof AmqpException) {
            BASE_PREFIX_MQ.error(e);
            return DataResult.fail(MQ_ERROR);
        }
        if (e instanceof DataAccessException || e instanceof SQLException) {
            BASE_PREFIX_DB.error(e);
            return DataResult.fail(DB_ERROR);
        }
        if (e instanceof BusinessException) {
            BASE_PREFIX_BIZ.error(e);
            return DataResult.fail(BIZ_DEFAULT_ERROR);
        }
        BASE_PREFIX_UNKNOWN.error(e);
        return dataResult;
    }
}
