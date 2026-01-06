package io.github.mrpotatosse.core.utils;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Component
public class CriteriaUtil {
    public <T> Specification<T> all() {
        return empty();
    }

    public <T> Specification<T> search(String fieldName, Object value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(criteriaBuilder.function("str", String.class, root.get(fieldName))),
                        "%" + value.toString().toLowerCase() + "%");
    }

    public <T> Specification<T> search(Map<String, Object> values) {
        Specification<T> result = null;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            result = result == null ? search(entry.getKey(), entry.getValue()) : result.or(search(entry.getKey(),
                    entry.getValue()));
        }
        return result;
    }

    public <T> Specification<T> by(String fieldName, Object value) {
        return by(root -> root.get(fieldName), value);
    }

    public <T> Specification<T> by(Function<Root<T>, Path<T>> field, Object value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(field.apply(root), value);
    }

    public <T, ID extends Serializable> Specification<T> in(String fieldName, Collection<ID> value) {
        return in(root -> root.get(fieldName), value);
    }

    public <T, ID extends Serializable> Specification<T> in(String fieldName, ID[] value) {
        return in(root -> root.get(fieldName), value);
    }

    public <T, ID extends Serializable> Specification<T> in(Function<Root<T>, Path<T>> field, ID[] value) {
        return in(field, Arrays.asList(value));
    }

    public <T, ID extends Serializable> Specification<T> in(Function<Root<T>, Path<T>> field,
                                                            Collection<ID> value) {
        return (root, query, cb) -> {
            Expression<Object> orderExpr =
                    cb.selectCase()
                            .when(field.apply(root).in(value), 0)
                            .otherwise(1);
            if (query != null) {
                query.orderBy(cb.asc(orderExpr));
            }
            return field.apply(root).in(value);
        };
    }

    @SafeVarargs
    public final <T extends Serializable> Specification<T> and(Specification<T>... specs) {
        Specification<T> result = null;
        for (Specification<T> spec : specs) {
            result = result == null ? spec : result.and(spec);
        }
        return result;
    }

    @SafeVarargs
    public final <T extends Serializable> Specification<T> or(Specification<T>... specs) {
        Specification<T> result = null;
        for (Specification<T> spec : specs) {
            result = result == null ? spec : result.or(spec);
        }
        return result;
    }


    // ORDER
    /*public <T, ID extends Serializable> Specification<T> inOrderFirst(
            Function<Root<T>, Path<T>> field,
            Collection<ID> value
    ) {
        return in(field, value).and((root, query, criteriaBuilder) -> {
            Expression<Object> orderExpr =
                    criteriaBuilder.selectCase()
                            .when(field.apply(root).in(value), 0)
                            .otherwise(1);

            assert query != null;
            query.orderBy(criteriaBuilder.asc(orderExpr));
            return criteriaBuilder.conjunction();
        });
    }

    // ORDER
    public <T, ID extends Serializable> Specification<T> inOrderFirst(
            String field,
            Collection<ID> value
    ) {
        return inOrderFirst(root -> root.get(field), value);
    }

    public <T, ID extends Serializable> Specification<T> inOrderFirst(
            String field,
            ID[] value
    ) {
        return inOrderFirst(root -> root.get(field), Arrays.asList(value));
    }

    public <T, ID extends Serializable> Specification<T> multiIn(Specification<T> initialSpec,
                                                                 Map<String, ID[]> values) {
        Specification<T> result = null;
        for (Map.Entry<String, ID[]> entry : values.entrySet()) {
            result = result == null ? in(entry.getKey(), entry.getValue()) : result.or(in(entry.getKey(),
                    entry.getValue()));
        }
        return result != null ? result.and(initialSpec) : initialSpec;
    }*/

    public <T> Specification<T> empty() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.conjunction();
    }
}
