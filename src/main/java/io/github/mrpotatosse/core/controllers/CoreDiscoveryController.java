package io.github.mrpotatosse.core.controllers;

import io.github.mrpotatosse.core.controllers.projections.DiscoveryDataProjection;
import io.github.mrpotatosse.core.enumerations.CoreEntityType;
import io.github.mrpotatosse.core.services.CoreService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/core")
@RequiredArgsConstructor
public class CoreDiscoveryController {
    @NonNull
    private final CoreService coreService;

    @GetMapping("reference")
    public ResponseEntity<Collection<DiscoveryDataProjection>> getReferences(@AuthenticationPrincipal Jwt principal) {
        return ResponseEntity.ok(coreService.getBeanForDiscovery(CoreEntityType.REFERENCE, principal));
    }

    @GetMapping("data")
    public ResponseEntity<Collection<DiscoveryDataProjection>> getData(@AuthenticationPrincipal Jwt principal) {
        return ResponseEntity.ok(coreService.getBeanForDiscovery(CoreEntityType.DATA, principal));
    }
}
