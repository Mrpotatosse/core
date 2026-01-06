package io.github.mrpotatosse.core.entities.helpers;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TableColumn {
    private String name;
    private Integer order;
    private boolean searchable;
    private boolean sortable;
}
