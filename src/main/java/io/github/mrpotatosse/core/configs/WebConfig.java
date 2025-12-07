package io.github.mrpotatosse.core.configs;

import io.github.mrpotatosse.core.interceptors.RestDisabledInterceptor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
@ComponentScan(basePackages = {"io.github.mrpotatosse.core"})
public class WebConfig implements WebMvcConfigurer {
    @NonNull
    private RestDisabledInterceptor restDisabledInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restDisabledInterceptor);
    }
}
