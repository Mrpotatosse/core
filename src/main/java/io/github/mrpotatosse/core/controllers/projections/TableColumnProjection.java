package io.github.mrpotatosse.core.controllers.projections;

public interface TableColumnProjection {
    String getName();

    Integer getOrder();

    Boolean getSearchable();

    Boolean getSortable();
}
