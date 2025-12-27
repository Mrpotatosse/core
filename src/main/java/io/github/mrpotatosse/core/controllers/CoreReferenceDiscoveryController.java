package io.github.mrpotatosse.core.controllers;

import io.github.mrpotatosse.core.services.CoreService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/core/references")
@RequiredArgsConstructor
public class CoreReferenceDiscoveryController {
    @NonNull
    private final CoreService coreService;

    @GetMapping
    @PreAuthorize("hasRole(@roleUtil.referenceManagerRole)")
    public ResponseEntity<Collection<String>> getReferences() {
        return ResponseEntity.ok(coreService.getReferencesBeanForDiscovery());
    }
}
