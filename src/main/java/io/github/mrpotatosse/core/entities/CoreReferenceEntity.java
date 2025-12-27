package io.github.mrpotatosse.core.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
@SoftDelete(columnName = "is_deleted")
public abstract class CoreReferenceEntity extends CoreEntity<Long> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
