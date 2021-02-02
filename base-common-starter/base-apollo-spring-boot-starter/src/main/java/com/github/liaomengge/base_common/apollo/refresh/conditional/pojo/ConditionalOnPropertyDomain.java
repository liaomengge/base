package com.github.liaomengge.base_common.apollo.refresh.conditional.pojo;

import lombok.Data;

import java.util.Objects;

/**
 * Created by liaomengge on 2021/2/1.
 */
@Data
public class ConditionalOnPropertyDomain {

    private String beanName;
    private Class<?> beanClass;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConditionalOnPropertyDomain that = (ConditionalOnPropertyDomain) o;
        return Objects.equals(beanName, that.beanName) &&
                Objects.equals(beanClass, that.beanClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, beanClass);
    }
}
