package io.github.mrpotatosse.core.utils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CriteriaUtil {
    public <T> Specification<T> all() {
        return null;
    }

    public <T> Specification<T> by(String fieldName, Object value) {
        return by(root -> root.get(fieldName), value);
    }

    public <T> Specification<T> by(Function<Root<T>, Path<T>> field, Object value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(field.apply(root), value);
    }
}
