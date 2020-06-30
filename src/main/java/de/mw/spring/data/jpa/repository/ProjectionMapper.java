package de.mw.spring.data.jpa.repository;

import javax.persistence.Tuple;

/**
 * Mapper interface for mapping {@link Tuple} to target type
 *
 * @param <P> input type of mapper
 * @param <R> produced type of mapper
 */
@FunctionalInterface
public interface ProjectionMapper<P,R> {
    
    /**
     * Transforms an input value e.g. a {@link Tuple} to the target type
     * 
     * @param tuple input of projection query
     * @return object of target type
     */
    R toModel(P tuple);
    
}