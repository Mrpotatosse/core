package io.github.mrpotatosse.core.controllers;

import io.github.mrpotatosse.core.annotations.controllers.RestDisabled;
import io.github.mrpotatosse.core.entities.CoreDataEntity;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

public abstract class CoreDataController<
        T extends CoreDataEntity,
        R extends CoreRepository<T, Long>> extends CoreController<T, R> {

    public CoreDataController(@NonNull CoreService coreService,
                              @NonNull ObjectUtil objectUtil,
                              @NonNull CriteriaUtil criteriaUtil,
                              @NonNull ControllerUtil controllerUtil) {
        super(coreService, objectUtil, criteriaUtil, controllerUtil);
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    @GetMapping
    public ResponseEntity<Page<?>> get(Pageable pageable,
                                       @RequestParam(name = "searchValue", defaultValue = "") String searchValue,
                                       @RequestParam(name = "ids", defaultValue = "") Long[] ids,
                                       @RequestParam(name = "asAdmin", defaultValue = "false") Boolean asAdmin,
                                       @AuthenticationPrincipal Jwt principal) {
        if (controllerUtil.hasRole(principal, controllerUtil.getAdminRole(this)) && asAdmin) {
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

        return ResponseEntity.ok(coreService
                .getAll(getRepository(),
                        criteriaUtil.by("userId", principal.getSubject()),
                        pageable,
                        getHelper().output(),
                        getInput(),
                        searchValue,
                        "id",
                        ids));
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id,
                                 @RequestParam(name = "asAdmin", defaultValue = "false") Boolean asAdmin,
                                 @AuthenticationPrincipal Jwt principal) {
        if (controllerUtil.hasRole(principal, controllerUtil.getAdminRole(this)) && asAdmin) {
            return ResponseEntity.ok(coreService
                    .getBy(getRepository(), criteriaUtil.by("id", id),
                            getHelper().output()));
        }

        return ResponseEntity.ok(coreService
                .getBy(getRepository(), criteriaUtil.and(criteriaUtil.by("id", id),
                                criteriaUtil.by("userId", principal.getSubject())),
                        getHelper().output()));
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'POST')")
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Map<String, Object> body,
                                  @AuthenticationPrincipal Jwt principal) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(coreService
                        .create(getRepository(),
                                getInput(),
                                body,
                                getHelper().output(),
                                principal.getSubject()));
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'PATCH')")
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    @PatchMapping("{id}")
    public ResponseEntity<?> patch(@PathVariable("id") Long id,
                                   @RequestBody Map<String, Object> body,
                                   @RequestParam(name = "asAdmin", defaultValue = "false") Boolean asAdmin,
                                   @AuthenticationPrincipal Jwt principal) {
        if (controllerUtil.hasRole(principal, controllerUtil.getAdminRole(this)) && asAdmin) {
            return ResponseEntity
                    .accepted()
                    .body(coreService.
                            update(getRepository(),
                                    criteriaUtil.by("id", id),
                                    body,
                                    getHelper().output()));
        }

        return ResponseEntity
                .accepted()
                .body(coreService.
                        update(getRepository(),
                                criteriaUtil.and(criteriaUtil.by("id", id),
                                        criteriaUtil.by("userId", principal.getSubject())),
                                body,
                                getHelper().output()));
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'DELETE')")
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id,
                                       @RequestParam(name = "asAdmin", defaultValue = "false") Boolean asAdmin,
                                       @AuthenticationPrincipal Jwt principal) {
        if (controllerUtil.hasRole(principal, controllerUtil.getAdminRole(this)) && asAdmin) {
            coreService.delete(getRepository(),
                    coreService.getBy(getRepository(), criteriaUtil.by("id", id)));
        } else {
            coreService.delete(getRepository(), coreService.getBy(getRepository(),
                    criteriaUtil.and(criteriaUtil.by("id", id),
                            criteriaUtil.by("userId", principal.getSubject()))));
        }
        return ResponseEntity.noContent().build();
    }
}
