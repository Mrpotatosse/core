package io.github.mrpotatosse.core.configs;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * This class is the main config of the core.
 */
@AutoConfiguration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@ComponentScan(basePackages = {"io.github.mrpotatosse.core"})
public class CoreConfig {
    @Bean
    public ProjectionFactory projection() {
        return new SpelAwareProxyProjectionFactory();
    }

    @Bean
    public ExpressionParser expressionParser() {
        return new SpelExpressionParser();
    }
}
