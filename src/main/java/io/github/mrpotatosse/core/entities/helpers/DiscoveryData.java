package io.github.mrpotatosse.core.entities.helpers;

import io.github.mrpotatosse.core.enumerations.CoreEntityType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DiscoveryData {
    String name;
    String[] paths;
    CoreEntityType type;
}
