package io.github.mrpotatosse.core.utils;

import io.github.mrpotatosse.core.annotations.controllers.Reference;
import io.github.mrpotatosse.core.controllers.CoreReferenceController;
import io.github.mrpotatosse.core.entities.CoreReferenceEntity;
import io.github.mrpotatosse.core.repositories.CoreRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReferenceUtil {
    @NonNull
    private final ApplicationContext applicationContext;

    private <T extends CoreReferenceEntity, R extends CoreRepository<T, Long>>
    ParameterizedType getControllerType(CoreReferenceController<T, R> controller) {
        Class<?> controllerClass = AopUtils.getTargetClass(controller);
        return (ParameterizedType) controllerClass.getGenericSuperclass();
    }

    public <T extends CoreReferenceEntity, R extends CoreRepository<T, Long>>
    Class<T> getReferenceInput(CoreReferenceController<T, R> controller) {
        @SuppressWarnings("unchecked")
        Class<T> inputClass = (Class<T>) getControllerType(controller).getActualTypeArguments()[0];
        return inputClass;
    }

    public <T extends CoreReferenceEntity, R extends CoreRepository<T, Long>>
    Class<R> getReferenceRepository(CoreReferenceController<T, R> controller) {
        @SuppressWarnings("unchecked")
        Class<R> repositoryClass = (Class<R>) getControllerType(controller).getActualTypeArguments()[1];
        return repositoryClass;
    }

    @SuppressWarnings("unchecked")
    public <T extends CoreReferenceEntity, R extends CoreRepository<T, Long>>
    R getEntityRepository(T entity) {
        Map<String, ?> beans = applicationContext.getBeansOfType(CoreRepository.class);
        for (Map.Entry<String, ?> entry : beans.entrySet()) {
            Class<?> repositoryClass = AopUtils.getTargetClass(entry.getValue());
            ParameterizedType parameterizedType = (ParameterizedType) repositoryClass.getGenericSuperclass();

            if (parameterizedType.getActualTypeArguments()[0].equals(entity.getClass())) {
                return (R) entry.getValue();
            }
        }

        return null;
    }

    public <T extends CoreReferenceEntity, R extends CoreRepository<T, Long>>
    boolean checkIfIgnoredMethodExists(CoreReferenceController<T, R> controller, final RequestMethod method) {
        if (Objects.isNull(method)) {
            return false;
        }
        return Arrays.asList(Objects.requireNonNull(controller
                        .getClass()
                        .getAnnotation(Reference.class)).disabledMethods())
                .contains(method);
    }

    public <T extends CoreReferenceEntity, R extends CoreRepository<T, Long>>
    boolean checkIfIgnoredMethodExists(CoreReferenceController<T, R> controller, final String methodName) {
        return checkIfIgnoredMethodExists(controller, RequestMethod.valueOf(methodName));
    }
}
