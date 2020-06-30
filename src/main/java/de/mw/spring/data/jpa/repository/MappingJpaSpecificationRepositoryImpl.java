package de.mw.spring.data.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingJpaSpecificationRepositoryImpl<T, ID> extends ProjectingJpaSpecificationRepositoryImpl<T, ID> 
                                                          implements MappingJpaSpecificationRepository<T, ID> {

    /**
     * Constructor for usage as a replacement of SimpleJpaRepository in {@link EnableJpaRepositories#repositoryBaseClass()}
     */
    public MappingJpaSpecificationRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }
    
    /**
     * Constructor for usage as standalone impl. bean impl.
     * 
     * @param domainClass JPA entity class
     * @param entityManager the entityManger of the JPA entity
     */
    public MappingJpaSpecificationRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }
    

    @Override
    public <R> Optional<R> findOne(@Nullable Specification<T> spec, Projection<T,Tuple> projection, ProjectionMapper<Tuple,R> mapper) {
        return findOne(spec, Sort.unsorted(), projection, mapper);
    }

    @Override
    public <R> Optional<R> findOne(@Nullable Specification<T> spec, Sort sort, Projection<T,Tuple> projection, ProjectionMapper<Tuple,R> mapper) {
        return findOne(spec, sort, Tuple.class, projection, mapper);
    }
    
    @Override
    public <R,P> Optional<R> findOne(@Nullable Specification<T> spec, Sort sort, Class<P> projectionClass, Projection<T,P> projection, ProjectionMapper<P,R> mapper) {
        return findOne(spec, sort, projectionClass, projection).map(mapper::toModel);
    }


    @Override
    public <R> List<R> findAll(@Nullable Specification<T> spec, Projection<T,Tuple> projection, ProjectionMapper<Tuple,R> mapper) {
        return findAll(spec, Sort.unsorted(), projection, mapper);
    } 

    @Override
    public <R> List<R> findAll(@Nullable Specification<T> spec, Sort sort, Projection<T,Tuple> projection, ProjectionMapper<Tuple,R> mapper) {
        return findAll(spec, sort, Tuple.class, projection, mapper);
    }
    
    @Override
    public <R,P> List<R> findAll(@Nullable Specification<T> spec, Sort sort, Class<P> projectionClass, Projection<T,P> projection, ProjectionMapper<P,R> mapper) {
        return transformTupleList(findAll(spec, sort, projectionClass, projection), mapper);
    }


    @Override
    public <R> Stream<R> findAllStream(@Nullable Specification<T> spec, Projection<T,Tuple> projection, ProjectionMapper<Tuple,R> mapper) {
        return findAllStream(spec, Sort.unsorted(), projection, mapper);
    }
    
    @Override
    public <R> Stream<R> findAllStream(@Nullable Specification<T> spec, Sort sort, Projection<T,Tuple> projection, ProjectionMapper<Tuple,R> mapper) {
        return findAllStream(spec, sort, Map.of(), projection, mapper);
    }
    
    @Override
    public <R> Stream<R> findAllStream(@Nullable Specification<T> spec, Sort sort, Map<String, Object> queryHints, Projection<T,Tuple> projection, ProjectionMapper<Tuple,R> mapper) {
        return findAllStream(spec, sort, queryHints, Tuple.class, projection, mapper);
    }
    
    @Override
    public <R,P> Stream<R> findAllStream(@Nullable Specification<T> spec, Sort sort, Map<String, Object> queryHints, Class<P> projectionClass, Projection<T,P> projection, ProjectionMapper<P,R> mapper) {
        return findAllStream(spec, sort, queryHints, projectionClass, projection).map(mapper::toModel);
    }


    @Override
    public <R> Page<R> findAll(@Nullable Specification<T> spec, Pageable pageable, Projection<T,Tuple> projection, ProjectionMapper<Tuple,R> mapper) {
        return findAll(spec, pageable, Tuple.class, projection, mapper);
    }
    
    @Override
    public <R,P> Page<R> findAll(@Nullable Specification<T> spec, Pageable pageable, Class<P> projectionClass, Projection<T,P> projection, ProjectionMapper<P,R> mapper) {
        Page<P> page = findAll(spec, pageable, projectionClass, projection);
        return new PageImpl<>(transformTupleList(page.getContent(), mapper), page.getPageable(), page.getTotalElements());
    }


    protected <R,P> List<R> transformTupleList(List<P> list, ProjectionMapper<P,R> mapper) {
        return list.stream()
                   .map(mapper::toModel)
                   .collect(Collectors.toList());
    }

}