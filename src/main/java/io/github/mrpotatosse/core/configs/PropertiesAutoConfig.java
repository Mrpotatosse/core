package io.github.mrpotatosse.core.configs;

import io.github.mrpotatosse.core.properties.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SecurityProperties.class})
public class PropertiesAutoConfig {
}
