package io.github.mrpotatosse.core.utils;

import io.github.mrpotatosse.core.annotations.controllers.ControllerHelper;
import io.github.mrpotatosse.core.annotations.controllers.DataControllerHelper;
import io.github.mrpotatosse.core.controllers.CoreController;
import io.github.mrpotatosse.core.converters.KeycloakJwtRolesConverter;
import io.github.mrpotatosse.core.repositories.CoreRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ControllerUtil {
    @NonNull
    private final KeycloakJwtRolesConverter keycloakJwtRolesConverter;

    private <T extends Serializable, R extends CoreRepository<T, Long>>
    ParameterizedType getControllerType(CoreController<T, R> controller) {
        Class<?> controllerClass = AopUtils.getTargetClass(controller);
        return (ParameterizedType) controllerClass.getGenericSuperclass();
    }

    public <T extends Serializable, R extends CoreRepository<T, Long>>
    Class<T> getControllerInput(CoreController<T, R> controller) {
        @SuppressWarnings("unchecked")
        Class<T> inputClass = (Class<T>) getControllerType(controller).getActualTypeArguments()[0];
        return inputClass;
    }

    public <T extends Serializable, R extends CoreRepository<T, Long>>
    Class<R> getControllerRepository(CoreController<T, R> controller) {
        @SuppressWarnings("unchecked")
        Class<R> repositoryClass = (Class<R>) getControllerType(controller).getActualTypeArguments()[1];
        return repositoryClass;
    }

    public String getRequiredRole(Object controller) {
        return Objects.requireNonNull(AnnotationUtils.findAnnotation(
                AopUtils.getTargetClass(controller), ControllerHelper.class)).requiredRole();
    }

    public String getAdminRole(Object controller) {
        return Objects.requireNonNull(AnnotationUtils.findAnnotation(
                AopUtils.getTargetClass(controller), DataControllerHelper.class)).adminRole();
    }

    public boolean checkIfIgnoredMethodExists(Object controller, final RequestMethod method) {
        if (Objects.isNull(method)) {
            return false;
        }
        return Arrays.asList(Objects.requireNonNull(AnnotationUtils.findAnnotation(
                        AopUtils.getTargetClass(controller), ControllerHelper.class))
                .disabledMethods()).contains(method);
    }

    public boolean checkIfIgnoredMethodExists(Object controller, final String methodName) {
        return checkIfIgnoredMethodExists(controller, RequestMethod.valueOf(methodName));
    }

    public boolean hasRole(Jwt principal, String role) {
        return Objects.requireNonNull(keycloakJwtRolesConverter.convert(principal))
                .stream()
                .anyMatch(a -> a.getAuthority().equals(KeycloakJwtRolesConverter.PREFIX_RESOURCE_ROLE + role));
    }
}
