package com.github.liaomengge.base_common.utils.clazz;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 16/9/23.
 */
@UtilityClass
public class LyClazzUtil {

    private final int STACK_TRACE_ELEMENT_TWO_INDEX = 2; //表示第二层的调用
    private final int STACK_TRACE_ELEMENT_THREE_INDEX = 3; //表示第三层的调用

    public String getCallClassName() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        if (stacktrace.length > STACK_TRACE_ELEMENT_THREE_INDEX) {
            return stacktrace[STACK_TRACE_ELEMENT_THREE_INDEX].getClassName();
        }
        return StringUtils.EMPTY;
    }

    public String getCallMethodName() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        if (stacktrace.length > STACK_TRACE_ELEMENT_THREE_INDEX) {
            StackTraceElement element = stacktrace[STACK_TRACE_ELEMENT_THREE_INDEX];
            return element.getClassName() + '.' + element.getMethodName() + "()";
        }
        return StringUtils.EMPTY;
    }

    public String getCurrentClassName() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        if (stacktrace.length > STACK_TRACE_ELEMENT_TWO_INDEX) {
            return stacktrace[STACK_TRACE_ELEMENT_TWO_INDEX].getClassName();
        }
        return StringUtils.EMPTY;
    }

    public String getCurrentMethodName() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        if (stacktrace.length > STACK_TRACE_ELEMENT_TWO_INDEX) {
            StackTraceElement element = stacktrace[STACK_TRACE_ELEMENT_TWO_INDEX];
            return element.getClassName() + '.' + element.getMethodName() + "()";
        }
        return StringUtils.EMPTY;
    }
}
