package com.github.liaomengge.base_common.utils.aop;

import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;

/**
 * Created by liaomengge on 2020/11/6.
 */
@UtilityClass
public class LyExpressionUtil {

    public static final String AND = " && ";
    public static final String OR = " || ";
    public static final String NOT = "!";

    public String getThisExpression(Class<?>... clazz) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < clazz.length; i++) {
            sBuilder.append("this(" + clazz[i].getTypeName() + ")");
            if (i != clazz.length - 1) {
                sBuilder.append(OR);
            }
        }
        return sBuilder.toString();
    }

    public String getWithinExpression(Class<?>... clazz) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < clazz.length; i++) {
            sBuilder.append("within(" + clazz[i].getTypeName() + ")");
            if (i != clazz.length - 1) {
                sBuilder.append(OR);
            }
        }
        return sBuilder.toString();
    }

    public String getTargetExpression(Class<?>... clazz) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < clazz.length; i++) {
            sBuilder.append("target(" + clazz[i].getTypeName() + ")");
            if (i != clazz.length - 1) {
                sBuilder.append(OR);
            }
        }
        return sBuilder.toString();
    }

    public String getMethodAnnotationExpression(Class<? extends Annotation>... annotations) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < annotations.length; i++) {
            sBuilder.append("@annotation(" + annotations[i].getTypeName() + ")");
            if (i != annotations.length - 1) {
                sBuilder.append(OR);
            }
        }
        return sBuilder.toString();
    }

    public String getWithinAnnotationExpression(Class<? extends Annotation>... annotations) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < annotations.length; i++) {
            sBuilder.append("@within(" + annotations[i].getTypeName() + ")");
            if (i != annotations.length - 1) {
                sBuilder.append(OR);
            }
        }
        return sBuilder.toString();
    }

    public String getTargetAnnotationExpression(Class<? extends Annotation>... annotations) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < annotations.length; i++) {
            sBuilder.append("@target(" + annotations[i].getTypeName() + ")");
            if (i != annotations.length - 1) {
                sBuilder.append(OR);
            }
        }
        return sBuilder.toString();
    }

    public String getPackagesExpression(String... packages) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < packages.length; i++) {
            sBuilder.append("execution( * " + packages[i] + "..*.*(..))");
            if (i != packages.length - 1) {
                sBuilder.append(OR);
            }
        }
        return sBuilder.toString();
    }

    public String getNonStaticExpression() {
        return "execution(public !static * *(..))";
    }

    public String and(String... expressions) {
        return operate(AND, expressions);
    }

    public String or(String... expressions) {
        return operate(OR, expressions);
    }

    private String operate(String appendOperate, String... expressions) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < expressions.length; i++) {
            sBuilder.append(expressions[i]);
            if (i != expressions.length - 1) {
                sBuilder.append(appendOperate);
            }
        }
        return sBuilder.toString();
    }
}
