package io.github.mrpotatosse.core.controllers;

import io.github.mrpotatosse.core.annotations.controllers.Reference;
import io.github.mrpotatosse.core.annotations.controllers.RestDisabled;
import io.github.mrpotatosse.core.entities.CoreReferenceEntity;
import io.github.mrpotatosse.core.projections.ReferenceColumnProjection;
import io.github.mrpotatosse.core.projections.ReferencePropertyProjection;
import io.github.mrpotatosse.core.repositories.CoreRepository;
import io.github.mrpotatosse.core.services.CoreService;
import io.github.mrpotatosse.core.utils.CriteriaUtil;
import io.github.mrpotatosse.core.utils.ObjectUtil;
import io.github.mrpotatosse.core.utils.ReferenceUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CoreReferenceController<
        T extends CoreReferenceEntity,
        R extends CoreRepository<T, Long>> {
    @NonNull
    private final CoreService coreService;
    @NonNull
    private final ObjectUtil objectUtil;
    @NonNull
    private final CriteriaUtil criteriaUtil;
    @NonNull
    private final ReferenceUtil referenceUtil;

    private Reference getReference() {
        return objectUtil.getAnnotation(this, Reference.class);
    }

    private R getRepository() {
        return coreService.getRepository(referenceUtil.getReferenceRepository(this));
    }

    @RestDisabled(condition = "@referenceUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping
    public ResponseEntity<Page<?>> get(Pageable pageable,
                                       @RequestParam(name = "searchValue", defaultValue = "") String searchValue,
                                       @RequestParam(name = "ids", defaultValue = "") Long[] ids) {
        return ResponseEntity.ok(coreService
                .getAll(getRepository(),
                        criteriaUtil.all(),
                        pageable,
                        getReference().output(),
                        referenceUtil.getReferenceInput(this),
                        searchValue,
                        "id",
                        ids));
    }

    @RestDisabled(condition = "@referenceUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping("{id}")
    @PreAuthorize("hasRole(@roleUtil.referenceManagerRole)")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(coreService
                .getBy(getRepository(), criteriaUtil.by("id", id), getReference().output()));
    }

    @RestDisabled(condition = "@referenceUtil.checkIfIgnoredMethodExists(#target, 'POST')")
    @PostMapping
    @PreAuthorize("hasRole(@roleUtil.referenceManagerRole)")
    public ResponseEntity<?> post(@RequestBody Map<String, Object> body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(coreService
                        .create(getRepository(),
                                referenceUtil.getReferenceInput(this),
                                body,
                                getReference().output()));
    }

    @RestDisabled(condition = "@referenceUtil.checkIfIgnoredMethodExists(#target, 'PATCH')")
    @PatchMapping("{id}")
    @PreAuthorize("hasRole(@roleUtil.referenceManagerRole)")
    public ResponseEntity<?> patch(@PathVariable("id") Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity
                .accepted()
                .body(coreService.
                        update(getRepository(),
                                criteriaUtil.by("id", id),
                                body,
                                getReference().output()));
    }

    @RestDisabled(condition = "@referenceUtil.checkIfIgnoredMethodExists(#target, 'DELETE')")
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole(@roleUtil.referenceManagerRole)")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        coreService.delete(getRepository(), id);
        return ResponseEntity.noContent().build();
    }

    @RestDisabled(condition = "@referenceUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping("table/default")
    @PreAuthorize("hasRole(@roleUtil.referenceManagerRole)")
    public ResponseEntity<?> getDefaultInstance() {
        return ResponseEntity.ok(coreService
                .defaultInstance(referenceUtil
                        .getReferenceInput(this), getReference().output()));
    }

    @RestDisabled(condition = "@referenceUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping("table/creation-properties")
    @PreAuthorize("hasRole(@roleUtil.referenceManagerRole)")
    public ResponseEntity<Collection<ReferencePropertyProjection>> getCreationProperties() {
        return ResponseEntity.ok(coreService
                .getCreationProperties(referenceUtil.getReferenceInput(this)));
    }

    @RestDisabled(condition = "@referenceUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping("table/modification-properties")
    @PreAuthorize("hasRole(@roleUtil.referenceManagerRole)")
    public ResponseEntity<Collection<ReferencePropertyProjection>> getModificationProperties() {
        return ResponseEntity.ok(coreService
                .getModificationProperties(referenceUtil.getReferenceInput(this)));
    }

    @RestDisabled(condition = "@referenceUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping("table/columns")
    @PreAuthorize("hasRole(@roleUtil.referenceManagerRole)")
    public ResponseEntity<Collection<ReferenceColumnProjection>> getColumns() {
        return ResponseEntity.ok(coreService
                .getReferenceColumns(referenceUtil.getReferenceInput(this)));
    }
}
