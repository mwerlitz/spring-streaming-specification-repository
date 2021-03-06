package de.mw.spring.data.jpa.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import java.util.List;

/**
 * ProjectionConstructor for {@link CriteriaQuery#multiselect(List)} tuple query.
 *
 * @param <T> type of entity
 * @param <P> type of projection
 */
@FunctionalInterface
public interface Projection<T, P> {
    
    /**
     * Produces the selection items for tuple projection.
     * Called within query construction.
     * 
     * @param root root of the entity
     * @param query the tuple criteria query
     * @param criteriaBuilder the criteria builder
     * @return List of selections
     */
    List<Selection<?>> toSelections(Root<T> root, CriteriaQuery<P> query, CriteriaBuilder criteriaBuilder);
    
}
