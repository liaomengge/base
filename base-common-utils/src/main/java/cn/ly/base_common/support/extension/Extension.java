package cn.ly.base_common.support.extension;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2019/10/15.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {

    /**
     * Implementation ID
     **/
    String value() default "";

    /**
     * The smaller the order number, the higher the position in the returned instance list.
     */
    int order() default Integer.MAX_VALUE;

    /**
     * SPI category, matching according to category when obtaining SPI list.
     * <p>
     * When there is a search-category to be filtered in category, the matching is successful.
     */
    String[] category() default "";
}
