package io.github.mrpotatosse.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * This interface is the base for any entity repository.
 *
 * @param <Entity> repository entity
 * @param <ID>     entity id type
 */
@NoRepositoryBean
public interface CoreRepository<Entity, ID extends Serializable>
        extends JpaRepository<Entity, ID>, JpaSpecificationExecutor<Entity> {
}
