package io.github.mrpotatosse.core.annotations.controllers;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Reference {
    Class<?> output() default void.class;

    /**
     * Use this variable to disable GET, POST, PATCH or DELETE.
     *
     * @return the disabled methods.
     */
    RequestMethod[] disabledMethods() default {};
}
