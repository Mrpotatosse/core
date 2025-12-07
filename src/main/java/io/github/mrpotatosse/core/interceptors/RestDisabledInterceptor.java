package io.github.mrpotatosse.core.interceptors;

import io.github.mrpotatosse.core.annotations.controllers.RestDisabled;
import io.github.mrpotatosse.core.resolvers.AllBeanResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class RestDisabledInterceptor implements HandlerInterceptor {
    @NonNull
    private ExpressionParser expressionParser;
    @NonNull
    private AllBeanResolver allBeanResolver;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler)
            throws Exception {

        if (handler instanceof HandlerMethod method) {
            RestDisabled restDisabled = method.getMethodAnnotation(RestDisabled.class);
            if (restDisabled != null && !restDisabled.condition().isBlank()) {
                StandardEvaluationContext context = new StandardEvaluationContext(method.getBean());
                context.setBeanResolver(allBeanResolver);
                context.setVariable("target", method.getBean());

                Boolean result = expressionParser
                        .parseExpression(restDisabled.condition())
                        .getValue(context, Boolean.class);

                if (Boolean.TRUE.equals(result)) {
                    response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value(), "Method '" + request.getMethod() + "' is not supported.");
                    return false; // stop request here
                }
            }
        }
        return true;
    }
}