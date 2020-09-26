package cn.ly.service.base_framework.common.consts;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by liaomengge on 2016/8/30.
 */
public class ServiceConst {

    public interface ResponseStatus {

        @Getter
        @AllArgsConstructor
        enum SuccCodeEnum {
            SUCCESS("000000", "成功");

            private String code;
            private String msg;
        }

        @Getter
        @AllArgsConstructor
        enum BizCodeEnum {
            BIZ_DEFAULT("000100", "业务处理失败");

            private String code;
            private String msg;
        }

        @Getter
        @AllArgsConstructor
        enum ErrorCodeEnum {
            DATA_ERROR("000200", "数据处理异常"),
            DB_ERROR("000201", "数据库处理异常"),
            REDIS_ERROR("000202", "Redis处理异常"),
            MQ_ERROR("000203", "MQ处理异常"),
            PARAM_ERROR("000300", "参数错误"),
            RPC_ERROR("000400", "RPC调用失败"),
            SERVER_ERROR("000500", "服务器内部错误"),
            SIGN_ERROR("000600", "签名失败"),
            FAIL_FAST_ERROR("000700", "接口暂不可用"),
            SERVER_BUSY_ERROR("000800", "服务器繁忙,请稍后再试");

            private String code;
            private String msg;
        }
    }

}