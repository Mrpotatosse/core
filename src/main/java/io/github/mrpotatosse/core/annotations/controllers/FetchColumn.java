package io.github.mrpotatosse.core.annotations.controllers;

import org.intellij.lang.annotations.Language;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FetchColumn {
    @Language(value = "HTTP")
    String fetch() default "";

    String fetchValueKey() default "";

    /**
     * Used to display a property of fetch object return.
     *
     * @return property of object return.
     */
    String display() default "";

    @Language(value = "HTTP")
    @AliasFor(attribute = "fetch")
    String value() default "";
}
