package io.github.mrpotatosse.core.annotations.controllers;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DisplayColumn {
    int order() default 1000;

    boolean searchable() default false;

    boolean sortable() default false;

    /**
     * Used to display another property of the projection.
     *
     * @return property of projection.
     */
    String display() default "";

    @AliasFor(attribute = "order")
    int value() default 1000;
}
