package io.github.mrpotatosse.core.controllers.projections;

public interface EntityPropertyProjection {
    String getName();

    String getType();

    Integer getOrder();

    String getFetch();

    String getFetchValueKey();

    String getDisplay();

    Boolean isRequired();

    Long getMin();

    Long getMax();
}
