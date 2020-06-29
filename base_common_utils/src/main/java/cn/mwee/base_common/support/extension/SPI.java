package cn.mwee.base_common.support.extension;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2019/10/15.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    /**
     * Default implementation ID
     **/
    String value() default "";

    /**
     * Declares whether a new object needs to be created each time an
     * implementation class is acquired, that is, whether it is singleton
     **/
    boolean single() default false;
}
