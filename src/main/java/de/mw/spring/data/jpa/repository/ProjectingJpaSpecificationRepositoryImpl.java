package de.mw.spring.data.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ProjectingJpaSpecificationRepositoryImpl<T, ID> extends StreamingJpaSpecificationRepositoryImpl<T, ID> 
                                                             implements ProjectingJpaSpecificationRepository<T, ID>  {

    private final EntityManager entityManager;

    /**
     * Constructor for usage as a replacement of SimpleJpaRepository in {@link EnableJpaRepositories#repositoryBaseClass()}
     */
    public ProjectingJpaSpecificationRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }
    
    /**
     * Constructor for usage as standalone impl. bean impl.
     * 
     * @param domainClass JPA entity class
     * @param entityManager the entityManger of the JPA entity
     */
    public ProjectingJpaSpecificationRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }


    @Override
    public Optional<Tuple> findOne(@Nullable Specification<T> spec, Projection<T, Tuple> projection) {
        return findOne(spec, Sort.unsorted(), projection);
    }

    @Override
    public Optional<Tuple> findOne(@Nullable Specification<T> spec, Sort sort, Projection<T, Tuple> projection) {
        return findOne(spec, sort, Tuple.class, projection);
    }
    
    @Override
    public <P> Optional<P> findOne(@Nullable Specification<T> spec, Sort sort, Class<P> projectionClass, Projection<T,P> projection) {
        TypedQuery<P> query = createProjectionQuery(spec, sort, projectionClass, projection);
        
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }


    @Override
    public List<Tuple> findAll(@Nullable Specification<T> spec, Projection<T,Tuple> projection) {
        return findAll(spec, Sort.unsorted(), projection);
    }
    
    @Override
    public List<Tuple> findAll(@Nullable Specification<T> spec, Sort sort, Projection<T,Tuple> projection) {
        return findAll(spec, sort, Tuple.class, projection);
    }
    
    @Override
    public <P> List<P> findAll(@Nullable Specification<T> spec, Sort sort, Class<P> projectionClass, Projection<T,P> projection) {
        return createProjectionQuery(spec, sort, projectionClass, projection).getResultList();
    }


    @Override
    public Stream<Tuple> findAllStream(@Nullable Specification<T> spec, Projection<T,Tuple> projection) {
        return findAllStream(spec, Sort.unsorted(), projection);
    }
    
    @Override
    public Stream<Tuple> findAllStream(@Nullable Specification<T> spec, Sort sort, Projection<T,Tuple> projection) {
        return findAllStream(spec, sort, Map.of(), projection);
    }
    
    @Override
    public Stream<Tuple> findAllStream(@Nullable Specification<T> spec, Sort sort, Map<String, Object> queryHints, Projection<T,Tuple> projection) {
        return findAllStream(spec, sort, queryHints, Tuple.class, projection);
    }
    
    @Override
    public <P> Stream<P> findAllStream(@Nullable Specification<T> spec, Sort sort, Map<String, Object> queryHints, Class<P> projectionClass, Projection<T,P> projection) {
        TypedQuery<P> query = createProjectionQuery(spec, sort, projectionClass, projection);
        queryHints.forEach((hintName, value) -> query.setHint(hintName, value));
        return query.getResultStream();
    }


    @Override
    public Page<Tuple> findAll(@Nullable Specification<T> spec, Pageable pageable, Projection<T,Tuple> projection) {
        return findAll(spec, pageable, Tuple.class, projection);
    }

    @Override
    public <P> Page<P> findAll(@Nullable Specification<T> spec, Pageable pageable, Class<P> projectionClass, Projection<T,P> projection) {
        if (pageable.isUnpaged()) {
            return new PageImpl<>(findAll(spec, pageable.getSort(), projectionClass, projection));
        }
        
        TypedQuery<P> q = createProjectionQuery(spec, pageable.getSort(), projectionClass, projection);
        if (pageable.isPaged()) {
            q.setFirstResult((int) pageable.getOffset());
            q.setMaxResults(pageable.getPageSize());
        }
        
        return PageableExecutionUtils.getPage(q.getResultList(), pageable, () -> executeCountQuery(getCountQuery(spec)));
    }


    protected <P> TypedQuery<P> createProjectionQuery(@Nullable Specification<T> spec, Sort sort, Class<P> projectionClass, Projection<T,P> projection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<P> cq = cb.createQuery(projectionClass);
        
        // FROM
        Root<T> root = cq.from(getDomainClass());
         
        // WHERE
        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, cq, cb);
            if (predicate != null) {
                cq.where(predicate);
            }
        }
        
        // SELECT (after WHERE, because specifications can provide a selection to the query, but this only works with a matching projectionType)
        if (cq.getSelection() == null) {
            cq.multiselect(projection.toSelections(root, cq, cb));
        }
        
        // ORDER BY
        if (sort.isSorted()) {
            cq.orderBy(QueryUtils.toOrders(sort, root, cb));
        }
        
        return entityManager.createQuery(cq);
    }
    
    // Note: Similar to {@link SimpleJpaRepository}
    protected TypedQuery<Long> getCountQuery(@Nullable Specification<T> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<T> root = cq.from(getDomainClass());
        
        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, cq, cb);
            if (predicate != null) {
                cq.where(predicate);
            }
        }

        if (cq.isDistinct()) {
            cq.select(cb.countDistinct(root));
        } else {
            cq.select(cb.count(root));
        }

        // Remove all Orders the Specifications might have applied
        cq.orderBy(Collections.<Order> emptyList());

        return entityManager.createQuery(cq);
    }

    // Note: Copy from {@link SimpleJpaRepository}
    protected long executeCountQuery(TypedQuery<Long> query) {
        List<Long> totals = query.getResultList();
        long total = 0L;
        
        for (Long element : totals) {
            total += element == null ? 0 : element;
        }
        
        return total;
    }

}