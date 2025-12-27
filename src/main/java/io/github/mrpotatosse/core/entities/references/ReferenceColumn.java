package io.github.mrpotatosse.core.entities.references;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReferenceColumn {
    private String name;
    private Integer order;
    private boolean searchable;
    private boolean sortable;
}
