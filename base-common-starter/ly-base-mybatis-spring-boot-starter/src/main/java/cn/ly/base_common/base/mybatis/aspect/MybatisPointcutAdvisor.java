package cn.ly.base_common.base.mybatis.aspect;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

import javax.annotation.PostConstruct;

/**
 * Created by liaomengge on 2019/7/5.
 */
public class MybatisPointcutAdvisor extends AbstractPointcutAdvisor {
    private static final long serialVersionUID = 7750782644079477406L;

    private final String dsKeys;
    private final boolean defaultMaster;
    private final String mapperPackage;

    public MybatisPointcutAdvisor(String dsKeys, boolean defaultMaster, String mapperPackage) {
        this.dsKeys = dsKeys;
        this.defaultMaster = defaultMaster;
        this.mapperPackage = mapperPackage;
    }

    private Pointcut pointcut;
    private Advice advice;

    @PostConstruct
    private void init() {
        AspectJExpressionPointcut expressionPointcut = new AspectJExpressionPointcut();
        expressionPointcut.setExpression("execution(* " + this.mapperPackage + "..*.*(..))");
        this.pointcut = expressionPointcut;
        this.advice = new MybatisAdvice(this.dsKeys, this.defaultMaster);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }
}
