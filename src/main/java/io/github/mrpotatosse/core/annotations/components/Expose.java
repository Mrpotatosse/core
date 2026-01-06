package io.github.mrpotatosse.core.annotations.components;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Inherited
public @interface Expose {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
