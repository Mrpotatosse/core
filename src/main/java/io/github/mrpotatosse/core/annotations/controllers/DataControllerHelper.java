package io.github.mrpotatosse.core.annotations.controllers;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface DataControllerHelper {
    String adminRole() default "";
}
