package de.mw.spring.data.jpa.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Extension to {@link JpaSpecificationExecutor} that allows to stream the result.
 *
 * @param <T> entity type
 * @param <ID> entity id type
 */
@NoRepositoryBean
public interface StreamingJpaSpecificationRepository<T, ID> extends Repository<T, ID> {

    /**
     * Returns all entities matching the given {@link Specification}.
     *
     * @param spec can be {@literal null}.
     * @return never {@literal null}.
     */
    Stream<T> findAllStream(@Nullable Specification<T> spec);

    /**
     * Returns all entities matching the given {@link Specification} and {@link Sort}.
     *
     * @param spec can be {@literal null}.
     * @param sort must not be {@literal null}.
     * @return never {@literal null}.
     */
    Stream<T> findAllStream(@Nullable Specification<T> spec, Sort sort);

    /**
     * Returns all entities matching the given {@link Specification} and {@link Sort}.
     *
     * @param spec can be {@literal null}.
     * @param sort must not be {@literal null}.
     * @param queryHints must not be {@literal null}.
     * @return never {@literal null}.
     */
    Stream<T> findAllStream(Specification<T> spec, Sort sort, Map<String, Object> queryHints);

}
