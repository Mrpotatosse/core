package io.github.mrpotatosse.core.annotations.controllers;

import io.github.mrpotatosse.core.enumerations.CoreEntityType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ControllerHelper {
    Class<?> output() default void.class;

    /**
     * Use this variable to disable GET, POST, PATCH or DELETE.
     *
     * @return the disabled methods.
     */
    RequestMethod[] disabledMethods() default {};

    String discovery() default "";

    String requiredRole();

    CoreEntityType type() default CoreEntityType.UNDEFINED;
}
