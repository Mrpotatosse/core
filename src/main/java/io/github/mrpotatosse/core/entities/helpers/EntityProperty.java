package io.github.mrpotatosse.core.entities.helpers;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EntityProperty {
    private String name;
    private String type;
    private Integer order;
    private String fetch;
    private String fetchValueKey;
    private String display;
    // validations
    private boolean required;
    private Long min;
    private Long max;
}
