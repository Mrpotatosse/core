package io.github.mrpotatosse.core.controllers;

import io.github.mrpotatosse.core.services.CoreService;
import io.github.mrpotatosse.core.utils.CoreUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoreController {
    @NonNull
    private final CoreUtil coreUtil;
    @NonNull
    private final CoreService coreService;
}
