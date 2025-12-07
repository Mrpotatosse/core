package io.github.mrpotatosse.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.core")
public class CoreProperties {
    private String referenceManagerRole;
}
