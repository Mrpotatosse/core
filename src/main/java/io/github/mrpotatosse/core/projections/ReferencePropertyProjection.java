package io.github.mrpotatosse.core.projections;

public interface ReferencePropertyProjection {
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
