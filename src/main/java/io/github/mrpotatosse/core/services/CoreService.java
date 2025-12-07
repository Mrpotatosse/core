package io.github.mrpotatosse.core.services;

import io.github.mrpotatosse.core.annotations.services.DisableCreation;
import io.github.mrpotatosse.core.annotations.services.DisableModification;
import io.github.mrpotatosse.core.exceptions.NotFoundException;
import io.github.mrpotatosse.core.repositories.CoreRepository;
import io.github.mrpotatosse.core.utils.CoreUtil;
import io.github.mrpotatosse.core.utils.ObjectUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoreService {
    @NonNull
    private final ApplicationContext context;
    @NonNull
    private final CoreUtil coreUtil;
    @NonNull
    private final ObjectUtil objectUtil;

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
            R extends CoreRepository<T, ID>,
            O>
    Page<O> getAll(R repository, Specification<T> specification, Pageable pageable, Class<O> output) {
        return repository.findAll(specification, pageable)
                .map(coreUtil.transformer(output));
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
        try {
            T instance = instanceClass
                    .getDeclaredConstructor()
                    .newInstance();
            return save(repository, objectUtil.modify(instance, properties, DisableCreation.class));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException("Creation failed", e);
        }
    }

    public <ID extends Serializable,
            T extends Serializable,
            R extends CoreRepository<T, ID>,
            O>
    O create(R repository, Class<T> instanceClass, final Map<String, Object> properties, Class<O> output) {
        try {
            T instance = instanceClass
                    .getDeclaredConstructor()
                    .newInstance();
            return save(repository, objectUtil.modify(instance, properties, DisableCreation.class), output);
        } catch (Exception e) {
            throw new IllegalArgumentException("Creation failed", e);
        }
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
}
