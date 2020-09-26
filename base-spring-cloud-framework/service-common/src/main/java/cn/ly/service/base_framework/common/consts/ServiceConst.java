package cn.ly.service.base_framework.common.consts;


import cn.ly.service.base_framework.base.code.SystemResultCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by liaomengge on 2016/8/30.
 * to see {@link SystemResultCode}
 */
@Deprecated
public class ServiceConst {

    @Deprecated
    public interface ResponseStatus {

        String getCode();

        String getDescription();

        @Getter
        @AllArgsConstructor
        @Deprecated
        enum SuccCodeEnum implements ResponseStatus {
            SUCCESS("000000", "成功");

            private String code;
            private String description;
        }

        @Getter
        @AllArgsConstructor
        @Deprecated
        enum BizCodeEnum implements ResponseStatus {
            //从100000开始都是业务异常
            BIZ_DEFAULT("100000", "业务处理失败");

            private String code;
            private String description;
        }

        @Getter
        @AllArgsConstructor
        @Deprecated
        enum ErrorCodeEnum implements ResponseStatus {
            DATA_ERROR("000100", "数据处理异常"),
            DB_ERROR("000201", "数据库处理异常"),
            REDIS_ERROR("000202", "Redis处理异常"),
            MQ_ERROR("000203", "MQ处理异常"),
            PARAM_ERROR("000300", "参数错误"),
            RPC_ERROR("000400", "RPC调用失败"),
            SERVER_ERROR("000500", "服务器内部错误"),
            SIGN_ERROR("000600", "签名失败"),
            FAIL_FAST_ERROR("000700", "接口暂不可用"),
            SERVER_BUSY_ERROR("000800", "服务器繁忙,请稍后再试"),
            UNKNOWN_ERROR("000900", "未知错误");

            private String code;
            private String description;
        }
    }

}