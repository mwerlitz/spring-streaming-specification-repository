package de.mw.spring.data.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Extension to {@link JpaSpecificationExecutor}, {@link StreamingJpaSpecificationRepository}
 * and {@link ProjectingJpaSpecificationRepository} that allows mapping of the projection result.
 *
 * @param <T> entity type
 * @param <ID> entity id type
 */
@NoRepositoryBean
public interface MappingJpaSpecificationRepository<T, ID> extends Repository<T, ID> {

    /**
     * Projecting and mapping version of {@link JpaSpecificationExecutor#findOne(Specification)}.
     * Returns a single mapped result matching the given {@link Specification} or {@link Optional#empty()} if none found.
     *
     * @param <R> type of mapped result
     * @param spec can be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @param mapper the tuple mapper to apply, must not be {@literal null}.
     * @return never {@literal null}.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if more than one entity found.
     */
    <R> Optional<R> findOne(@Nullable Specification<T> spec, Projection<T> projection, ProjectionMapper<R> mapper);

    /**
     * Projecting and mapping version of {@link JpaSpecificationExecutor#findOne(Specification)} with sort option.
     * Returns a single mapped result matching the given {@link Specification} or {@link Optional#empty()} if none found.
     *
     * @param <R> type of mapped result
     * @param spec can be {@literal null}.
     * @param sort the sorting to apply, must not be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @param mapper the tuple mapper to apply, must not be {@literal null}.
     * @return never {@literal null}.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if more than one entity found.
     */
    <R> Optional<R> findOne(@Nullable Specification<T> spec, Sort sort, Projection<T> projection, ProjectionMapper<R> mapper);

    /**
     * Projecting and mapping version of {@link JpaSpecificationExecutor#findAll(Specification)}.
     * Returns all mapped results matching the given {@link Specification}.
     *
     * @param <R> type of mapped result
     * @param spec can be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @param mapper the tuple mapper to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    <R> List<R> findAll(@Nullable Specification<T> spec, Projection<T> projection, ProjectionMapper<R> mapper);

    /**
     * Projecting and mapping version of {@link JpaSpecificationExecutor#findAll(Specification, Sort)}.
     * Returns all mapped results matching the given {@link Specification} and {@link Sort}.
     *
     * @param <R> type of mapped result
     * @param spec can be {@literal null}.
     * @param sort must not be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @param mapper the tuple mapper to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    <R> List<R> findAll(@Nullable Specification<T> spec, Sort sort, Projection<T> projection, ProjectionMapper<R> mapper);

    /**
     * Projecting and mapping version of {@link StreamingJpaSpecificationRepository#findAllStream(Specification)}.
     * Returns all mapped results matching the given {@link Specification}.
     *
     * @param spec can be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @param mapper the tuple mapper to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    <R> Stream<R> findAllStream(@Nullable Specification<T> spec, Projection<T> projection, ProjectionMapper<R> mapper);

    /**
     * Projecting and mapping version of {@link StreamingJpaSpecificationRepository#findAllStream(Specification, Sort)}.
     * Returns all mapped results matching the given {@link Specification} and {@link Sort}.
     *
     * @param <R> type of mapped result
     * @param spec can be {@literal null}.
     * @param sort must not be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @param mapper the tuple mapper to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    <R> Stream<R> findAllStream(@Nullable Specification<T> spec, Sort sort, Projection<T> projection, ProjectionMapper<R> mapper);

    /**
     * Projecting and mapping version of {@link StreamingJpaSpecificationRepository#findAllStream(Specification, Sort, Map)}.
     * Returns all mapped results matching the given {@link Specification} and {@link Sort}.
     *
     * @param <R> type of mapped result
     * @param spec can be {@literal null}.
     * @param sort must not be {@literal null}.
     * @param queryHints must not be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @param mapper the tuple mapper to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    <R> Stream<R> findAllStream(@Nullable Specification<T> spec, Sort sort, Map<String, Object> queryHints, Projection<T> projection, ProjectionMapper<R> mapper);

    /**
     * Projecting and mapping version of {@link JpaSpecificationExecutor#findAll(Specification, Pageable)}.
     * Returns a {@link Page} of mapped results matching the given {@link Specification}.
     *
     * @param <R> type of mapped result
     * @param spec can be {@literal null}.
     * @param pageable must not be {@literal null}.
     * @param projection the projection to apply, must not be {@literal null}.
     * @param mapper the tuple mapper to apply, must not be {@literal null}.
     * @return never {@literal null}.
     */
    <R> Page<R> findAll(@Nullable Specification<T> spec, Pageable pageable, Projection<T> projection, ProjectionMapper<R> mapper);

}