package cn.mwee.base_common.utils.string;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by liaomengge on 17/11/9.
 */
public final class MwToStringUtil {

    private MwToStringUtil() {
    }

    /**
     * 打印出子类和父类的属性, 效率略低
     *
     * @param object
     * @return
     */
    public static String toString(Object object) {
        return ToStringBuilder.reflectionToString(object, new CustomShortPrefixToStringStyle());
    }

    /**
     * 只打印子类的属性, 效率高
     *
     * @param object
     * @return
     */
    public static String toString2(Object object) {
        return MoreObjects.toStringHelper(object.getClass().getSimpleName()).omitNullValues().addValue(object).toString();
    }

    private static final class CustomShortPrefixToStringStyle extends ToStringStyle {

        private static final long serialVersionUID = 2448526386382326573L;

        public CustomShortPrefixToStringStyle() {
            super();
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
            this.setNullText(null);
        }

        private Object readResolve() {
            return ToStringStyle.SHORT_PREFIX_STYLE;
        }
    }
}
