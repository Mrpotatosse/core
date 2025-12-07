package io.github.mrpotatosse.core.annotations.controllers;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface RestDisabled {
    @Language(value = "SpEL")
    String condition() default "";
}
