package com.github.liaomengge.service.base_framework.common.exception;

import com.github.liaomengge.base_common.support.exception.AbstractAppException;
import com.github.liaomengge.base_common.support.exception.AbstractAppRuntimeException;
import com.github.liaomengge.base_common.support.exception.BusinessException;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.number.LyMoreNumberUtil;
import com.github.liaomengge.service.base_framework.base.DataResult;
import com.github.liaomengge.service.base_framework.base.code.SystemResultCode;
import org.redisson.client.RedisException;
import org.springframework.amqp.AmqpException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import redis.clients.jedis.exceptions.JedisException;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import static com.github.liaomengge.base_common.utils.log.LyAlarmLogUtil.MiddlewareEnum.*;
import static com.github.liaomengge.base_common.utils.log.LyAlarmLogUtil.ServerProjEnum.BASE_PREFIX_BIZ;
import static com.github.liaomengge.base_common.utils.log.LyAlarmLogUtil.ServerProjEnum.BASE_PREFIX_UNKNOWN;

/**
 * Created by liaomengge on 2018/10/23.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public DataResult defaultErrorHandler(Exception e) {
        DataResult dataResult = buildAlarm(e);
        if (e instanceof AbstractAppException) {
            dataResult.setSysCode(((AbstractAppException) e).getErrCode());
            dataResult.setSysMsg(((AbstractAppException) e).getErrMsg());
        }
        if (e instanceof AbstractAppRuntimeException) {
            dataResult.setSysCode(((AbstractAppRuntimeException) e).getErrCode());
            dataResult.setSysMsg(((AbstractAppRuntimeException) e).getErrMsg());
        }

        dataResult.setSysException(LyThrowableUtil.getStackTrace(e));
        return dataResult;
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (Objects.isNull(ex) && Objects.isNull(status)) {
            return super.handleExceptionInternal(ex, DataResult.fail(SystemResultCode.UNKNOWN_ERROR), headers, status, request);
        }
        String statusCode = Optional.ofNullable(status).map(val -> LyMoreNumberUtil.toString(val.value())).orElse("");
        String statusDesc = Optional.ofNullable(status).map(val -> val.getReasonPhrase()).orElse("");
        String sysErrDesc = "HttpStatus[" + statusCode + "], Error Message[" + statusDesc + "]";
        String sysException = Optional.ofNullable(ex).map(val -> LyThrowableUtil.getStackTrace(val)).orElse("");
        DataResult dataResult = DataResult.fail(SystemResultCode.UNKNOWN_ERROR, sysErrDesc, sysException);
        return super.handleExceptionInternal(ex, dataResult, headers, status, request);
    }

    private DataResult buildAlarm(Exception e) {
        DataResult dataResult = DataResult.fail(SystemResultCode.SERVER_ERROR);
        if (e instanceof SerializationException || e instanceof JedisException || e instanceof RedisException) {
            BASE_PREFIX_REDIS.error(e);
            return DataResult.fail(SystemResultCode.REDIS_ERROR);
        }
        if (e instanceof JmsException || e instanceof AmqpException) {
            BASE_PREFIX_MQ.error(e);
            return DataResult.fail(SystemResultCode.MQ_ERROR);
        }
        if (e instanceof TransactionException || e instanceof DataAccessException || e instanceof SQLException) {
            BASE_PREFIX_DB.error(e);
            return DataResult.fail(SystemResultCode.DB_ERROR);
        }
        if (e instanceof BusinessException) {
            BASE_PREFIX_BIZ.error(e);
            return DataResult.fail(SystemResultCode.BIZ_DEFAULT_ERROR);
        }
        BASE_PREFIX_UNKNOWN.error(e);
        return dataResult;
    }
}
