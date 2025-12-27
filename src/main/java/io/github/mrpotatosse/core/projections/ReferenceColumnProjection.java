package io.github.mrpotatosse.core.projections;

public interface ReferenceColumnProjection {
    String getName();

    Integer getOrder();

    Boolean getSearchable();

    Boolean getSortable();
}
