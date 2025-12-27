package io.github.mrpotatosse.core.utils;

import io.github.mrpotatosse.core.annotations.controllers.RetrieveWith;
import io.github.mrpotatosse.core.resolvers.AllBeanResolver;
import io.github.mrpotatosse.core.resolvers.ExposedBeanResolver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ObjectUtil {
    @NonNull
    private final ExpressionParser parser;
    @NonNull
    private final ExposedBeanResolver exposedBeanResolver;
    @NonNull
    private final AllBeanResolver allBeanResolver;

    public <T extends Serializable, A extends Annotation>
    T modify(T source, String fieldName, Object value, Class<A> disableAnnotation) {
        T output = SerializationUtils.clone(source);

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setRootObject(output);
        context.setBeanResolver(exposedBeanResolver);

        Field field = ReflectionUtils.findField(source.getClass(), fieldName);
        if (Objects.isNull(field))
            throw new IllegalArgumentException("Field '" + fieldName + "' not found");

        ReflectionUtils.makeAccessible(field);
        if (Objects.nonNull(disableAnnotation) && field.isAnnotationPresent(disableAnnotation))
            throw new IllegalArgumentException("Field '" + fieldName + "' modification is disabled");

        Expression exp = parser.parseExpression(fieldName);

        if (field.getAnnotation(RetrieveWith.class) instanceof RetrieveWith retrieveWith) {
            StandardEvaluationContext fullContext = new StandardEvaluationContext();
            fullContext.setRootObject(output);
            fullContext.setBeanResolver(allBeanResolver);
            fullContext.setVariable("value", value);
            exp.setValue(context, parser.parseExpression(retrieveWith.value()).getValue(fullContext));
        } else if (value instanceof String strValue) {
            exp.setValue(context, field.getType().isAssignableFrom(String.class) ?
                    strValue : parser.parseExpression(strValue).getValue(context));
        } else {
            exp.setValue(context, value);
        }

        return output;
    }

    public <T extends Serializable, A extends Annotation>
    T modify(T source, final Map<String, Object> fields, Class<A> disableAnnotation) {
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            source = modify(source, entry.getKey(), entry.getValue(), disableAnnotation);
        }
        return source;
    }

    public <A extends Annotation> A getAnnotation(Object source, Class<A> annotation) {
        return source.getClass().isAnnotationPresent(annotation) ?
                source.getClass().getAnnotation(annotation) : null;
    }

    public <T extends Serializable, A extends Annotation>
    Set<Field> getNonAnnotatedProperties(Class<T> sourceClass, Class<A> annotation) {
        Set<Field> fields = new HashSet<>();
        ReflectionUtils.doWithFields(sourceClass, fields::add);
        return fields.stream()
                .filter(f -> !f.isAnnotationPresent(annotation) && !Modifier.isStatic(f.getModifiers()))
                .collect(Collectors.toSet());
    }

    public <T extends Serializable, A extends Annotation>
    Set<Field> getAnnotatedProperties(Class<T> sourceClass, Class<A> annotation) {
        Set<Field> fields = new HashSet<>();
        ReflectionUtils.doWithFields(sourceClass, fields::add);
        return fields.stream()
                .filter(f -> f.isAnnotationPresent(annotation) && !Modifier.isStatic(f.getModifiers()))
                .collect(Collectors.toSet());
    }
}
