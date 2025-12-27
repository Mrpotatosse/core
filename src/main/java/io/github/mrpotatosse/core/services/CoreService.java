package io.github.mrpotatosse.core.services;

import io.github.mrpotatosse.core.annotations.controllers.DisplayColumn;
import io.github.mrpotatosse.core.annotations.controllers.FetchColumn;
import io.github.mrpotatosse.core.annotations.controllers.Reference;
import io.github.mrpotatosse.core.annotations.services.DisableCreation;
import io.github.mrpotatosse.core.annotations.services.DisableModification;
import io.github.mrpotatosse.core.entities.references.ReferenceColumn;
import io.github.mrpotatosse.core.entities.references.ReferenceProperty;
import io.github.mrpotatosse.core.exceptions.NotFoundException;
import io.github.mrpotatosse.core.projections.ReferenceColumnProjection;
import io.github.mrpotatosse.core.projections.ReferencePropertyProjection;
import io.github.mrpotatosse.core.repositories.CoreRepository;
import io.github.mrpotatosse.core.utils.CoreUtil;
import io.github.mrpotatosse.core.utils.CriteriaUtil;
import io.github.mrpotatosse.core.utils.ObjectUtil;
import jakarta.validation.constraints.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreService {
    @NonNull
    private final ApplicationContext context;
    @NonNull
    private final CoreUtil coreUtil;
    @NonNull
    private final ObjectUtil objectUtil;
    @NonNull
    private final CriteriaUtil criteriaUtil;

    public <ID extends Serializable,
            T,
            R extends CoreRepository<T, ID>>
    R getRepository(Class<R> repositoryClass) {
        return context.getBean(repositoryClass);
    }

    public <ID extends Serializable,
            T,
            R extends CoreRepository<T, ID>>
    Page<T> getAll(R repository, Specification<T> specification, Pageable pageable) {
        return repository
                .findAll(specification, pageable);
    }

    public <ID extends Serializable,
            T extends Serializable,
            R extends CoreRepository<T, ID>,
            O>
    Page<O> getAll(R repository,
                   Specification<T> specification,
                   Pageable pageable,
                   Class<O> output,
                   Class<T> input,
                   String search,
                   String idKey,
                   ID[] ids) {
        Specification<T> spec = specification;
        if (!search.isBlank()) {
            spec = spec.and(criteriaUtil.search(getSearchColumns(input, search)));
        }
        if (ids != null && ids.length > 0 && !idKey.isBlank()) {
            Specification<T> idSpec = criteriaUtil.in(idKey, ids);
            spec = spec.or(idSpec);
        }
        return repository.findAll(spec, pageable).map(coreUtil.transformer(output));
    }

    public <ID extends Serializable,
            T,
            R extends CoreRepository<T, ID>>
    T getBy(R repository, Specification<T> specification) {
        return repository.findOne(specification)
                .orElseThrow(() -> new NotFoundException("Reference not found"));
    }

    public <ID extends Serializable,
            T,
            R extends CoreRepository<T, ID>,
            O>
    O getBy(R repository, Specification<T> specification, Class<O> outputClass) {
        return repository.findOne(specification)
                .map(coreUtil.transformer(outputClass))
                .orElseThrow(() -> new NotFoundException("Reference not found"));
    }

    public <ID extends Serializable,
            T,
            R extends CoreRepository<T, ID>>
    T save(R repository, T entity) {
        return repository.save(entity);
    }

    public <ID extends Serializable,
            T,
            R extends CoreRepository<T, ID>,
            O>
    O save(R repository, T entity, Class<O> output) {
        return coreUtil.transformer(output)
                .apply(save(repository, entity));
    }

    public <ID extends Serializable,
            T,
            R extends CoreRepository<T, ID>>
    void delete(R repository, T entity) {
        repository.delete(entity);
    }

    public <ID extends Serializable,
            T,
            R extends CoreRepository<T, ID>>
    void delete(R repository, ID id) {
        repository.deleteById(id);
    }

    public <ID extends Serializable,
            T extends Serializable,
            R extends CoreRepository<T, ID>>
    T create(R repository, Class<T> instanceClass, final Map<String, Object> properties) {
        return save(repository, objectUtil.modify(defaultInstance(instanceClass), properties, DisableCreation.class));
    }

    public <ID extends Serializable,
            T extends Serializable,
            R extends CoreRepository<T, ID>,
            O>
    O create(R repository, Class<T> instanceClass, final Map<String, Object> properties, Class<O> output) {
        return save(repository,
                objectUtil.modify(defaultInstance(instanceClass),
                        properties,
                        DisableCreation.class),
                output);
    }

    public <ID extends Serializable,
            T extends Serializable,
            R extends CoreRepository<T, ID>>
    T update(R repository, Specification<T> specification, final Map<String, Object> properties) {
        T instance = getBy(repository, specification);
        return save(repository, objectUtil.modify(instance, properties, DisableModification.class));
    }

    public <ID extends Serializable,
            T extends Serializable,
            R extends CoreRepository<T, ID>,
            O>
    O update(R repository, Specification<T> specification, final Map<String, Object> properties, Class<O> output) {
        T instance = getBy(repository, specification);
        return save(repository, objectUtil.modify(instance, properties, DisableModification.class), output);
    }

    public <T> T defaultInstance(Class<T> instanceClass) {
        try {
            return instanceClass
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Creation failed", e);
        }
    }

    public <T, O> O defaultInstance(Class<T> instanceClass, Class<O> output) {
        return coreUtil.transformer(output).apply(defaultInstance(instanceClass));
    }

    public Collection<String> getReferencesBeanForDiscovery() {
        return context.getBeansWithAnnotation(Reference.class)
                .values()
                .stream()
                .map(o -> o
                        .getClass()
                        .getAnnotation(Reference.class) instanceof Reference ref ?
                        ref.discovery() : "")
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toList());
    }

    public <T extends Serializable>
    Collection<ReferencePropertyProjection> getCreationProperties(Class<T> referenceClass) {
        return objectUtil.getNonAnnotatedProperties(referenceClass, DisableCreation.class)
                .stream()
                .map(this::transformFieldToProperty)
                .filter(Objects::nonNull)
                .map(coreUtil.transformer(ReferencePropertyProjection.class))
                .collect(Collectors.toSet());
    }

    public <T extends Serializable>
    Collection<ReferencePropertyProjection> getModificationProperties(Class<T> referenceClass) {
        return objectUtil.getNonAnnotatedProperties(referenceClass, DisableModification.class)
                .stream()
                .map(this::transformFieldToProperty)
                .filter(Objects::nonNull)
                .map(coreUtil.transformer(ReferencePropertyProjection.class))
                .collect(Collectors.toSet());
    }

    public ReferenceProperty transformFieldToProperty(Field field) {
        ReferenceProperty.ReferencePropertyBuilder builder = ReferenceProperty.builder();
        if (field.getAnnotation(DisplayColumn.class) instanceof DisplayColumn column) {
            builder = builder.order(column.order());
        } else {
            builder = builder.order(1000);
        }

        if (field.getAnnotation(FetchColumn.class) instanceof FetchColumn column) {
            builder = builder
                    .fetch(column.fetch().isBlank() ? null : column.fetch())
                    .fetchValueKey(column.fetchValueKey().isBlank() ? null : column.fetchValueKey())
                    .display(column.display().isBlank() ? null : column.display());
        }

        if (field.getAnnotation(Min.class) instanceof Min min) {
            builder = builder.min(min.value());
        }
        if (field.getAnnotation(Max.class) instanceof Max max) {
            builder = builder.max(max.value());
        }

        return builder
                .name(field.getName())
                .type(field.getGenericType().getTypeName())
                .required(field.isAnnotationPresent(NotNull.class)
                        || field.isAnnotationPresent(NotEmpty.class)
                        || field.isAnnotationPresent(NotBlank.class))
                .build();
    }

    public ReferenceColumn transformFieldToColumn(Field field) {
        ReferenceColumn.ReferenceColumnBuilder builder = ReferenceColumn.builder();
        if (field.getAnnotation(DisplayColumn.class) instanceof DisplayColumn column) {
            builder = builder
                    .order(column.order())
                    .searchable(column.searchable())
                    .sortable(column.sortable())
                    .name(column.display().isBlank() ? field.getName() : column.display());
        } else {
            builder = builder.order(1000)
                    .searchable(false)
                    .sortable(false)
                    .name(field.getName());
        }
        return builder.build();
    }

    public <T extends Serializable>
    Collection<ReferenceColumnProjection> getReferenceColumns(Class<T> referenceClass) {
        return objectUtil.getAnnotatedProperties(referenceClass, DisplayColumn.class)
                .stream()
                .map(this::transformFieldToColumn)
                .filter(Objects::nonNull)
                .map(coreUtil.transformer(ReferenceColumnProjection.class))
                .collect(Collectors.toSet());
    }

    public <T extends Serializable>
    Map<String, Object> getSearchColumns(Class<T> referenceClass, Object value) {
        return objectUtil.getAnnotatedProperties(referenceClass, DisplayColumn.class)
                .stream()
                .filter(field ->
                        field.getAnnotation(DisplayColumn.class) instanceof DisplayColumn column && column.searchable())
                .collect(Collectors.toMap(Field::getName, f -> value));
    }
}
