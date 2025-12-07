package io.github.mrpotatosse.core.resolvers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AllBeanResolver implements BeanResolver {
    @NonNull
    private final ApplicationContext applicationContext;

    @Override
    public @NonNull Object resolve(@NonNull EvaluationContext context, @NonNull String beanName)
            throws AccessException {
        try {
            return applicationContext.getBean(beanName);
        } catch (BeansException e) {
            throw new AccessException("Failed to resolve bean: " + beanName, e);
        }
    }
}
