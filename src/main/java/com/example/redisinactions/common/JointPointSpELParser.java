package com.example.redisinactions.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class JointPointSpELParser {

  public String parseSpEL(ProceedingJoinPoint pjp, String spEL) {
    final MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.setRootObject(pjp);

    // Registering method parameters as SpEL variables
    Object[] args = pjp.getArgs();
    String[] paramNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(methodSignature.getMethod());
    if (paramNames != null) {
      for (int i = 0; i < paramNames.length; i++) {
        context.setVariable(paramNames[i], args[i]);
      }
    }

    // Parse and evaluate the expression
    ExpressionParser parser = new SpelExpressionParser();
    return parser.parseExpression(spEL).getValue(context, String.class);
  }

}