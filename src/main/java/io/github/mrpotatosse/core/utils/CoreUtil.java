package io.github.mrpotatosse.core.utils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class CoreUtil {
    @NonNull
    private final ProjectionFactory projection;

    public <T, U> Function<T, U> transformer(final Class<U> clazz) {
        return (e) -> projection.createProjection(clazz, e);
    }

    public <T, U> Collection<U> transform(final T[] values, final Class<U> clazz) {
        return Arrays.stream(values)
                .map(transformer(clazz))
                .toList();
    }

    public <T, U> Collection<U> transform(final Collection<T> values, final Class<U> clazz) {
        return values.stream()
                .map(transformer(clazz))
                .toList();
    }
}
