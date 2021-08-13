package com.github.liaomengge.base_common.utils.spel;

import lombok.experimental.UtilityClass;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Created by liaomengge on 2021/7/17.
 */
@UtilityClass
public class LySpelUtil {

    private static SpelExpressionParser parser = new SpelExpressionParser();

    public <T> T parse(String expr, Class<T> clazz) {
        return parse(expr, clazz, new StandardEvaluationContext());
    }

    public <T> T parse(String expr, Class<T> clazz, EvaluationContext evaluationContext) {
        Expression expression = parser.parseExpression(expr);
        return expression.getValue(evaluationContext, clazz);
    }

    public <T> T parseTemplate(String expr, Class<T> clazz) {
        return parseTemplate(expr, clazz, new StandardEvaluationContext());
    }

    public <T> T parseTemplate(String expr, Class<T> clazz, EvaluationContext evaluationContext) {
        return parseTemplate(expr, clazz, evaluationContext, new TemplateParserContext());
    }

    public <T> T parseTemplate(String expr, Class<T> clazz, ParserContext parserContext) {
        return parseTemplate(expr, clazz, new StandardEvaluationContext(), parserContext);
    }

    public <T> T parseTemplate(String expr, Class<T> clazz, EvaluationContext evaluationContext,
                               ParserContext parserContext) {
        Expression expression = parser.parseExpression(expr, parserContext);
        return expression.getValue(evaluationContext, clazz);
    }
}
