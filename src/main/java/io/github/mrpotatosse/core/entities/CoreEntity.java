package io.github.mrpotatosse.core.entities;

import io.github.mrpotatosse.core.annotations.services.DisableCreation;
import io.github.mrpotatosse.core.annotations.services.DisableModification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * This class is the base for any jpa entity.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class CoreEntity<ID extends Serializable> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DisableCreation
    @DisableModification
    private ID id;

    @Column(name = "created", nullable = false)
    @NotNull
    @DisableCreation
    @DisableModification
    private LocalDateTime created;

    @Column(name = "updated", nullable = false)
    @NotNull
    @DisableCreation
    @DisableModification
    private LocalDateTime updated;

    @PrePersist
    private void prePersist() {
        created = LocalDateTime.now();
        updated = LocalDateTime.now();

        onPrePersist();
    }

    @PreUpdate
    private void preUpdate() {
        updated = LocalDateTime.now();

        onPreUpdate();
    }

    protected void onPrePersist() {
        // do nothing
    }

    protected void onPreUpdate() {
        // do nothing
    }
}
