package cn.mwee.base_common.multi.shardingsphere.annotation;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2019/9/16.
 * mybatis Mapper的方法上, 打上此注解(sharding jdbc主从切换)
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HintMaster {
}
