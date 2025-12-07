package io.github.mrpotatosse.core.utils;

import io.github.mrpotatosse.core.properties.CoreProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleUtil {
    @NonNull
    private final CoreProperties coreProperties;

    public String getReferenceManagerRole() {
        return coreProperties.getReferenceManagerRole();
    }
}
