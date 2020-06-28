package de.mw.spring.data.jpa.repository;

import javax.persistence.Tuple;

/**
 * Mapper interface for mapping {@link Tuple} to target type
 *
 * @param <R> produced type of mapper
 */
@FunctionalInterface
public interface ProjectionMapper<R> {
    
    /**
     * Transforms a {@link Tuple}
     * 
     * @param tuple tuple of projection query
     * @return object of target type
     */
    R toModel(Tuple tuple);
    
}