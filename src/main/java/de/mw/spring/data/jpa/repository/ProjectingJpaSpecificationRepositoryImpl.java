package de.mw.spring.data.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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


    public ProjectingJpaSpecificationRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }
    
    public ProjectingJpaSpecificationRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }


    @Override
    public Optional<Tuple> findOne(@Nullable Specification<T> spec, Projection<T> projection) {
        return findOne(spec, Sort.unsorted(), projection);
    }

    @Override
    public Optional<Tuple> findOne(@Nullable Specification<T> spec, Sort sort, Projection<T> projection) {
        TypedQuery<Tuple> query = createProjectionQuery(spec, sort, projection);
        
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }  


    @Override
    public List<Tuple> findAll(@Nullable Specification<T> spec, Projection<T> projection) {
        return findAll(spec, Sort.unsorted(), projection);
    }
    
    @Override
    public List<Tuple> findAll(@Nullable Specification<T> spec, Sort sort, Projection<T> projection) {
        return createProjectionQuery(spec, sort, projection).getResultList();
    }


    @Override
    public Stream<Tuple> findAllStream(@Nullable Specification<T> spec, Projection<T> projection) {
        return findAllStream(spec, Sort.unsorted(), projection);
    }
    
    @Override
    public Stream<Tuple> findAllStream(@Nullable Specification<T> spec, Sort sort, Projection<T> projection) {
        return findAllStream(spec, sort, Map.of(), projection);
    }
    
    @Override
    public Stream<Tuple> findAllStream(@Nullable Specification<T> spec, Sort sort, Map<String, Object> queryHints, Projection<T> projection) {
        TypedQuery<Tuple> query = createProjectionQuery(spec, sort, projection);
        queryHints.forEach((hintName, value) -> query.setHint(hintName, value));
        return query.getResultStream();
    }


    @Override
    public Page<Tuple> findAll(@Nullable Specification<T> spec, Pageable pageable, Projection<T> projection) {
        if (pageable.isUnpaged()) {
            return new PageImpl<>(findAll(spec, pageable.getSort(), projection));
        }
        
        TypedQuery<Tuple> q = createProjectionQuery(spec, pageable.getSort(), projection);
        if (pageable.isPaged()) {
            q.setFirstResult((int) pageable.getOffset());
            q.setMaxResults(pageable.getPageSize());
        }
        
        return PageableExecutionUtils.getPage(q.getResultList(), pageable, () -> executeCountQuery(getCountQuery(spec)));
    }


    protected TypedQuery<Tuple> createProjectionQuery(@Nullable Specification<T> spec, Sort sort, Projection<T> projection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
        
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