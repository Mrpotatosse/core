package io.github.mrpotatosse.core.controllers;

import io.github.mrpotatosse.core.annotations.controllers.RestDisabled;
import io.github.mrpotatosse.core.entities.CoreReferenceEntity;
import io.github.mrpotatosse.core.repositories.CoreRepository;
import io.github.mrpotatosse.core.services.CoreService;
import io.github.mrpotatosse.core.utils.ControllerUtil;
import io.github.mrpotatosse.core.utils.CriteriaUtil;
import io.github.mrpotatosse.core.utils.ObjectUtil;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

public abstract class CoreReferenceController<
        T extends CoreReferenceEntity,
        R extends CoreRepository<T, Long>> extends CoreController<T, R> {

    public CoreReferenceController(@NonNull CoreService coreService,
                                   @NonNull ObjectUtil objectUtil,
                                   @NonNull CriteriaUtil criteriaUtil,
                                   @NonNull ControllerUtil controllerUtil) {
        super(coreService, objectUtil, criteriaUtil, controllerUtil);
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping
    public ResponseEntity<Page<?>> get(Pageable pageable,
                                       @RequestParam(name = "searchValue", defaultValue = "") String searchValue,
                                       @RequestParam(name = "ids", defaultValue = "") Long[] ids) {
        return ResponseEntity.ok(coreService
                .getAll(getRepository(),
                        criteriaUtil.all(),
                        pageable,
                        getHelper().output(),
                        getInput(),
                        searchValue,
                        "id",
                        ids));
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(coreService
                .getBy(getRepository(), criteriaUtil.by("id", id), getHelper().output()));
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'POST')")
    @PostMapping
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    public ResponseEntity<?> post(@RequestBody Map<String, Object> body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(coreService
                        .create(getRepository(),
                                getInput(),
                                body,
                                getHelper().output()));
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'PATCH')")
    @PatchMapping("{id}")
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    public ResponseEntity<?> patch(@PathVariable("id") Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity
                .accepted()
                .body(coreService.
                        update(getRepository(),
                                criteriaUtil.by("id", id),
                                body,
                                getHelper().output()));
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'DELETE')")
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        coreService.delete(getRepository(), id);
        return ResponseEntity.noContent().build();
    }
}
