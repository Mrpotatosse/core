package io.github.mrpotatosse.core.configs;

import io.github.mrpotatosse.core.controllers.CoreDiscoveryController;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@RequiredArgsConstructor
public class CoreControllerAutoConfig {
    @NonNull
    private final CoreDiscoveryController coreDiscoveryController;

    @Bean
    @ConditionalOnMissingBean
    public CoreDiscoveryController coreDiscoveryController() {
        return coreDiscoveryController;
    }
}
