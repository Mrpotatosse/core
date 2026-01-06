package io.github.mrpotatosse.core.controllers.projections;

import io.github.mrpotatosse.core.enumerations.CoreEntityType;

public interface DiscoveryDataProjection {
    String getName();

    String[] getPaths();

    CoreEntityType getType();
}
