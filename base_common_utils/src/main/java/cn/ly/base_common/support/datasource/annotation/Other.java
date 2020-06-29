package cn.ly.base_common.support.datasource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by liaomengge on 18/3/31.
 * mybatis Mapper的方法上, 打上此注解(自定义主从切换)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Other {
}
