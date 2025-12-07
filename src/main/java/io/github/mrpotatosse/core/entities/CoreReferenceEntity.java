package io.github.mrpotatosse.core.entities;

import io.github.mrpotatosse.core.annotations.services.DisableCreation;
import io.github.mrpotatosse.core.annotations.services.DisableModification;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class CoreReferenceEntity extends CoreEntity<Long> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "deleted")
    @DisableCreation
    @DisableModification
    private LocalDateTime deleted;

    public void delete() {
        this.deleted = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = null;
    }
}
