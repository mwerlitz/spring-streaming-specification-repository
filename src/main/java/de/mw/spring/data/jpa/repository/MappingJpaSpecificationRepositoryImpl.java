package de.mw.spring.data.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingJpaSpecificationRepositoryImpl<T, ID> extends ProjectingJpaSpecificationRepositoryImpl<T, ID> implements MappingJpaSpecificationRepository<T, ID> {

    
    public MappingJpaSpecificationRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }
    
    public MappingJpaSpecificationRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }
    

    @Override
    public <R> Optional<R> findOne(@Nullable Specification<T> spec, Projection<T> projection, ProjectionMapper<R> mapper) {
        return findOne(spec, Sort.unsorted(), projection, mapper);
    }

    @Override
    public <R> Optional<R> findOne(@Nullable Specification<T> spec, Sort sort, Projection<T> projection, ProjectionMapper<R> mapper) {
        return findOne(spec, sort, projection).map(mapper::toModel);
    } 


    @Override
    public <R> List<R> findAll(@Nullable Specification<T> spec, Projection<T> projection, ProjectionMapper<R> mapper) {
        return findAll(spec, Sort.unsorted(), projection, mapper);
    } 

    @Override
    public <R> List<R> findAll(@Nullable Specification<T> spec, Sort sort, Projection<T> projection, ProjectionMapper<R> mapper) {
        return transformTupleList(createProjectionQuery(spec, sort, projection).getResultList(), mapper);
    }


    @Override
    public <R> Stream<R> findAllStream(@Nullable Specification<T> spec, Projection<T> projection, ProjectionMapper<R> mapper) {
        return findAllStream(spec, Sort.unsorted(), projection, mapper);
    }
    
    @Override
    public <R> Stream<R> findAllStream(@Nullable Specification<T> spec, Sort sort, Projection<T> projection, ProjectionMapper<R> mapper) {
        return findAllStream(spec, sort, Map.of(), projection, mapper);
    }
    
    @Override
    public <R> Stream<R> findAllStream(@Nullable Specification<T> spec, Sort sort, Map<String, Object> queryHints, Projection<T> projection, ProjectionMapper<R> mapper) {
        return findAllStream(spec, sort, queryHints, projection).map(mapper::toModel);
    }


    @Override
    public <R> Page<R> findAll(@Nullable Specification<T> spec, Pageable pageable, Projection<T> projection, ProjectionMapper<R> mapper) {
        Page<Tuple> page = findAll(spec, pageable, projection);
        return new PageImpl<>(transformTupleList(page.getContent(), mapper), page.getPageable(), page.getTotalElements());
    }


    protected <R> List<R> transformTupleList(List<Tuple> list, ProjectionMapper<R> mapper) {
        return list.stream()
                   .map(mapper::toModel)
                   .collect(Collectors.toList());
    }

}