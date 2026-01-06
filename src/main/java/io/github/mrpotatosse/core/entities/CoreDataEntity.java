package io.github.mrpotatosse.core.entities;

import io.github.mrpotatosse.core.annotations.services.DisableCreation;
import io.github.mrpotatosse.core.annotations.services.DisableModification;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
@SoftDelete(columnName = "is_deleted")
public abstract class CoreDataEntity extends CoreEntity<Long> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id", nullable = false)
    @NotNull
    @DisableCreation
    @DisableModification
    private String userId;
}
