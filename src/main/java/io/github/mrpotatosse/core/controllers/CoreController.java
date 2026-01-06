package io.github.mrpotatosse.core.controllers;

import io.github.mrpotatosse.core.annotations.controllers.ControllerHelper;
import io.github.mrpotatosse.core.annotations.controllers.RestDisabled;
import io.github.mrpotatosse.core.controllers.projections.EntityPropertyProjection;
import io.github.mrpotatosse.core.controllers.projections.TableColumnProjection;
import io.github.mrpotatosse.core.repositories.CoreRepository;
import io.github.mrpotatosse.core.services.CoreService;
import io.github.mrpotatosse.core.utils.ControllerUtil;
import io.github.mrpotatosse.core.utils.CriteriaUtil;
import io.github.mrpotatosse.core.utils.ObjectUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.Serializable;
import java.util.Collection;

@RequiredArgsConstructor
public abstract class CoreController<
        T extends Serializable,
        R extends CoreRepository<T, Long>> {
    @NonNull
    protected final CoreService coreService;
    @NonNull
    protected final ObjectUtil objectUtil;
    @NonNull
    protected final CriteriaUtil criteriaUtil;
    @NonNull
    protected final ControllerUtil controllerUtil;

    protected ControllerHelper getHelper() {
        return objectUtil.getAnnotation(this, ControllerHelper.class);
    }

    protected R getRepository() {
        return coreService.getRepository(controllerUtil.getControllerRepository(this));
    }

    protected Class<T> getInput() {
        return controllerUtil.getControllerInput(this);
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping("table/default")
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    public ResponseEntity<?> getDefaultInstance() {
        return ResponseEntity.ok(coreService.defaultInstance(getInput(), getHelper().output()));
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping("table/creation-properties")
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    public ResponseEntity<Collection<EntityPropertyProjection>> getCreationProperties() {
        return ResponseEntity.ok(coreService
                .getCreationProperties(getInput()));
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping("table/modification-properties")
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    public ResponseEntity<Collection<EntityPropertyProjection>> getModificationProperties() {
        return ResponseEntity.ok(coreService
                .getModificationProperties(getInput()));
    }

    @RestDisabled(condition = "@controllerUtil.checkIfIgnoredMethodExists(#target, 'GET')")
    @GetMapping("table/columns")
    @PreAuthorize("hasRole(@controllerUtil.getRequiredRole(this))")
    public ResponseEntity<Collection<TableColumnProjection>> getColumns() {
        return ResponseEntity.ok(coreService
                .getReferenceColumns(getInput()));
    }
}
