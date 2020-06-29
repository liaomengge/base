package cn.mwee.service.base_framework.common.filter;

import cn.mwee.base_common.utils.validator.MwParamValidatorUtil;
import cn.mwee.service.base_framework.base.DataResult;
import cn.mwee.service.base_framework.base.StringRestResponse;
import cn.mwee.service.base_framework.common.filter.chain.FilterChain;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.annotation.Order;

import java.util.*;

import static cn.mwee.base_common.support.misc.consts.ToolConst.JOINER;
import static cn.mwee.service.base_framework.base.code.SystemResultCode.PARAM_ERROR;

/**
 * Created by liaomengge on 2018/11/22.
 */
@Order(2000)
public class ParamValidateFilter extends AbstractFilter {

    @Override
    public Object doFilter(ProceedingJoinPoint joinPoint, FilterChain chain) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (Objects.isNull(args) || args.length <= 0) {
            return chain.doFilter(joinPoint, chain);
        }
        List<String> checkResults = new ArrayList<>();
        Arrays.stream(args).filter(Objects::nonNull).forEach(val -> {
            Map<String, ArrayList<String>> validateMap = MwParamValidatorUtil.validate(val);
            if (Objects.nonNull(validateMap)) {
                validateMap.forEach((key, value) -> checkResults.add(String.format("参数:%s校验失败,原因:%s", key, value)));
            }
        });
        if (!checkResults.isEmpty()) {
            DataResult<StringRestResponse> dataResult = new DataResult<>(false);

            StringRestResponse restResponse = new StringRestResponse();
            restResponse.setErrNo(PARAM_ERROR.getCode());
            restResponse.setErrMsg(JOINER.join(checkResults));
            dataResult.setData(restResponse);
            return dataResult;
        }
        return chain.doFilter(joinPoint, chain);
    }
}
