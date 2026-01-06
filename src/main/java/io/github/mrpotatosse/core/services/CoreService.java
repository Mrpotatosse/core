package io.github.mrpotatosse.core.services;

import io.github.mrpotatosse.core.annotations.controllers.ControllerHelper;
import io.github.mrpotatosse.core.annotations.controllers.DisplayColumn;
import io.github.mrpotatosse.core.annotations.controllers.FetchColumn;
import io.github.mrpotatosse.core.annotations.services.DisableCreation;
import io.github.mrpotatosse.core.annotations.services.DisableModification;
import io.github.mrpotatosse.core.controllers.projections.DiscoveryDataProjection;
import io.github.mrpotatosse.core.controllers.projections.EntityPropertyProjection;
import io.github.mrpotatosse.core.controllers.projections.TableColumnProjection;
import io.github.mrpotatosse.core.entities.CoreDataEntity;
import io.github.mrpotatosse.core.entities.helpers.DiscoveryData;
import io.github.mrpotatosse.core.entities.helpers.EntityProperty;
import io.github.mrpotatosse.core.entities.helpers.TableColumn;
import io.github.mrpotatosse.core.enumerations.CoreEntityType;
import io.github.mrpotatosse.core.exceptions.NotFoundException;
import io.github.mrpotatosse.core.repositories.CoreRepository;
import io.github.mrpotatosse.core.utils.ControllerUtil;
import io.github.mrpotatosse.core.utils.CoreUtil;
import io.github.mrpotatosse.core.utils.CriteriaUtil;
import io.github.mrpotatosse.core.utils.ObjectUtil;
import jakarta.validation.constraints.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

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
    @NonNull
    private final ControllerUtil controllerUtil;

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
            T extends CoreDataEntity,
            R extends CoreRepository<T, ID>>
    T create(R repository, Class<T> instanceClass, final Map<String, Object> properties, Jwt user) {
        T instance = objectUtil.modify(defaultInstance(instanceClass), properties, DisableCreation.class);
        instance.setUserId(user.getSubject());
        return save(repository, instance);
    }

    public <ID extends Serializable,
            T extends Serializable,
            R extends CoreRepository<T, ID>,
            O>
    O create(R repository, Class<T> instanceClass, final Map<String, Object> properties, Class<O> output) {
        return save(repository,
                objectUtil.modify(defaultInstance(instanceClass), properties, DisableCreation.class),
                output);
    }

    public <ID extends Serializable,
            T extends CoreDataEntity,
            R extends CoreRepository<T, ID>,
            O>
    O create(R repository, Class<T> instanceClass, final Map<String, Object> properties, Class<O> output,
             String userId) {
        T instance = objectUtil.modify(defaultInstance(instanceClass), properties, DisableCreation.class);
        instance.setUserId(userId);
        return save(repository, instance, output);
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

    public DiscoveryData getDiscoveryDataFromController(Object controller, CoreEntityType type, Jwt principal) {
        Class<?> controllerClass = AopUtils.getTargetClass(controller);

        if (!controllerUtil.hasRole(principal, controllerUtil.getRequiredRole(controller))) {
            return null;
        }

        if (AnnotationUtils.findAnnotation(controllerClass, ControllerHelper.class) instanceof ControllerHelper ref
                && ref.type().equals(type)) {
            DiscoveryData.DiscoveryDataBuilder builder = DiscoveryData
                    .builder()
                    .type(ref.type())
                    .name(ref.discovery());

            if (AnnotationUtils.findAnnotation(controllerClass, RequestMapping.class) instanceof RequestMapping req) {
                builder = builder.paths(req.value());
            }
            return builder.build();
        }

        return null;
    }

    public Collection<DiscoveryDataProjection> getBeanForDiscovery(CoreEntityType type, Jwt principal) {
        return context.getBeansWithAnnotation(ControllerHelper.class)
                .values()
                .stream()
                .map(o -> this.getDiscoveryDataFromController(o, type, principal))
                .filter(Objects::nonNull)
                .map(coreUtil.transformer(DiscoveryDataProjection.class))
                .collect(Collectors.toList());
    }

    public <T extends Serializable>
    Collection<EntityPropertyProjection> getCreationProperties(Class<T> referenceClass) {
        return objectUtil.getNonAnnotatedProperties(referenceClass, DisableCreation.class)
                .stream()
                .map(this::transformFieldToProperty)
                .filter(Objects::nonNull)
                .map(coreUtil.transformer(EntityPropertyProjection.class))
                .collect(Collectors.toSet());
    }

    public <T extends Serializable>
    Collection<EntityPropertyProjection> getModificationProperties(Class<T> referenceClass) {
        return objectUtil.getNonAnnotatedProperties(referenceClass, DisableModification.class)
                .stream()
                .map(this::transformFieldToProperty)
                .filter(Objects::nonNull)
                .map(coreUtil.transformer(EntityPropertyProjection.class))
                .collect(Collectors.toSet());
    }

    public EntityProperty transformFieldToProperty(Field field) {
        EntityProperty.EntityPropertyBuilder builder = EntityProperty.builder();
        if (AnnotationUtils.findAnnotation(field, DisplayColumn.class) instanceof DisplayColumn column) {
            builder = builder.order(column.order());
        } else {
            builder = builder.order(1000);
        }

        if (AnnotationUtils.findAnnotation(field, FetchColumn.class) instanceof FetchColumn column) {
            builder = builder
                    .fetch(column.fetch().isBlank() ? null : column.fetch())
                    .fetchValueKey(column.fetchValueKey().isBlank() ? null : column.fetchValueKey())
                    .display(column.display().isBlank() ? null : column.display());
        }

        if (AnnotationUtils.findAnnotation(field, Min.class) instanceof Min min) {
            builder = builder.min(min.value());
        }
        if (AnnotationUtils.findAnnotation(field, Max.class) instanceof Max max) {
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

    public TableColumn transformFieldToColumn(Field field) {
        TableColumn.TableColumnBuilder builder = TableColumn.builder();
        if (AnnotationUtils.findAnnotation(field, DisplayColumn.class) instanceof DisplayColumn column) {
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
    Collection<TableColumnProjection> getReferenceColumns(Class<T> referenceClass) {
        return objectUtil.getAnnotatedProperties(referenceClass, DisplayColumn.class)
                .stream()
                .map(this::transformFieldToColumn)
                .filter(Objects::nonNull)
                .map(coreUtil.transformer(TableColumnProjection.class))
                .collect(Collectors.toSet());
    }

    public <T extends Serializable>
    Map<String, Object> getSearchColumns(Class<T> referenceClass, Object value) {
        return objectUtil.getAnnotatedProperties(referenceClass, DisplayColumn.class)
                .stream()
                .filter(field ->
                        AnnotationUtils.findAnnotation(field, DisplayColumn.class) instanceof DisplayColumn column &&
                                column.searchable())
                .collect(Collectors.toMap(Field::getName, f -> value));
    }
}
