package de.mw.spring.data.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.lang.Nullable;

import javax.persistence.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Extension to {@link JpaSpecificationExecutor} and {@link StreamingJpaSpecificationRepository} 
 * that allows simple projection of the result to {@link Tuple}s.
 *
 * @param <T> entity type
 * @param <ID> entity id type
 */
@NoRepositoryBean
public interface ProjectingJpaSpecificationRepository<T, ID> extends Repository<T, ID> {

    /**
     * Projecting version of {@link JpaSpecificationExecutor#findOne(Specification)}.
     * Returns a single projected tuple result matching the given {@link Specification} or {@link Optional#empty()} if none found.
     *
     * @param spec can be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @return never {@literal null}.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if more than one entity found.
     */
    Optional<Tuple> findOne(@Nullable Specification<T> spec, Projection<T> projection);

    /**
     * Projecting version of {@link JpaSpecificationExecutor#findOne(Specification)} with sort option.
     * Returns a single projected tuple result matching the given {@link Specification} or {@link Optional#empty()} if none found.
     *
     * @param spec can be {@literal null}.
     * @param sort the sorting to apply, must not be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @return never {@literal null}.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if more than one entity found.
     */
    Optional<Tuple> findOne(@Nullable Specification<T> spec, Sort sort, Projection<T> projection);

    /**
     * Projecting version of {@link JpaSpecificationExecutor#findAll(Specification)}.
     * Returns all projected tuple results matching the given {@link Specification}.
     *
     * @param spec can be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    List<Tuple> findAll(@Nullable Specification<T> spec, Projection<T> projection);

    /**
     * Projecting version of {@link JpaSpecificationExecutor#findAll(Specification, Sort)}.
     * Returns all projected tuple results matching the given {@link Specification} and {@link Sort}.
     *
     * @param spec can be {@literal null}.
     * @param sort must not be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    List<Tuple> findAll(@Nullable Specification<T> spec, Sort sort, Projection<T> projection);

    /**
     * Projecting version of {@link StreamingJpaSpecificationRepository#findAllStream(Specification)}.
     * Returns all projected tuple results matching the given {@link Specification}.
     *
     * @param spec can be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    Stream<Tuple> findAllStream(@Nullable Specification<T> spec, Projection<T> projection);

    /**
     * Projecting version of {@link StreamingJpaSpecificationRepository#findAllStream(Specification, Sort)}.
     * Returns all projected tuple results matching the given {@link Specification} and {@link Sort}.
     *
     * @param spec can be {@literal null}.
     * @param sort must not be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    Stream<Tuple> findAllStream(@Nullable Specification<T> spec, Sort sort, Projection<T> projection);

    /**
     * Projecting version of {@link StreamingJpaSpecificationRepository#findAllStream(Specification, Sort, Map)}.
     * Returns all projected tuple results matching the given {@link Specification} and {@link Sort}.
     *
     * @param spec can be {@literal null}.
     * @param sort must not be {@literal null}.
     * @param queryHints must not be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    Stream<Tuple> findAllStream(@Nullable Specification<T> spec, Sort sort, Map<String, Object> queryHints, Projection<T> projection);

    /**
     * Projecting version of {@link JpaSpecificationExecutor#findAll(Specification, Pageable)}.
     * Returns a {@link Page} of projected tuple results matching the given {@link Specification}.
     *
     * @param spec can be {@literal null}.
     * @param pageable must not be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    Page<Tuple> findAll(@Nullable Specification<T> spec, Pageable pageable, Projection<T> projection);

}