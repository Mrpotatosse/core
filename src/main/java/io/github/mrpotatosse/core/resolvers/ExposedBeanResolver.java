package io.github.mrpotatosse.core.resolvers;


import io.github.mrpotatosse.core.annotations.components.Expose;
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
public class ExposedBeanResolver implements BeanResolver {
    @NonNull
    private final ApplicationContext applicationContext;

    @Override
    public @NonNull Object resolve(@NonNull EvaluationContext context, @NonNull String beanName)
            throws AccessException {
        try {
            Object bean = applicationContext.getBean(beanName);
            Class<?> beanClass = applicationContext.getType(beanName);
            if (beanClass != null && beanClass.isAnnotationPresent(Expose.class)) {
                return bean;
            }
            throw new AccessException("Bean '" + beanName + "' is not exposed");
        } catch (BeansException e) {
            throw new AccessException("Failed to resolve bean: " + beanName, e);
        }
    }
}
