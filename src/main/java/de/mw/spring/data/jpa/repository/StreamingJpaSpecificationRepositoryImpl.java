package de.mw.spring.data.jpa.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.Map;
import java.util.stream.Stream;

public class StreamingJpaSpecificationRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> 
                                                            implements StreamingJpaSpecificationRepository<T, ID> {
    
    /**
     * Constructor for usage as a replacement of SimpleJpaRepository in {@link EnableJpaRepositories#repositoryBaseClass()}
     */
    public StreamingJpaSpecificationRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    /**
     * Constructor for usage as standalone impl. bean impl.
     * 
     * @param domainClass JPA entity class
     * @param entityManager the entityManger of the JPA entity
     */
    public StreamingJpaSpecificationRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }


    @Override
    public Stream<T> findAllStream(@Nullable Specification<T> spec) {
        return findAllStream(spec, Sort.unsorted());
    }

    @Override
    public Stream<T> findAllStream(@Nullable Specification<T> spec, Sort sort) {
        return findAllStream(spec, sort, Map.of());
    }

    @Override
    public Stream<T> findAllStream(@Nullable Specification<T> spec, Sort sort, Map<String, Object> queryHints) {
        TypedQuery<T> query = getQuery(spec, sort);
        queryHints.forEach((hintName, value) -> query.setHint(hintName, value));
        return query.getResultStream();
    }

}