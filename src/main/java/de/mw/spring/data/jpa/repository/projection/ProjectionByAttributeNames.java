package de.mw.spring.data.jpa.repository.projection;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.mw.spring.data.jpa.repository.Projection;

/**
 * Simple projection strategy by entity attribute names.
 * 
 * There is no check for the availability of the attributes.
 * So the produced query may fail at runtime.
 */
public class ProjectionByAttributeNames<T> implements Projection<T> {

    protected String[] attributes;
    
    public ProjectionByAttributeNames(String... attributes) {
        this.attributes = attributes;
    }
    
    @Override
    public List<Selection<?>> toSelections(Root<T> root, CriteriaQuery<Tuple> query, CriteriaBuilder criteriaBuilder) {
        return Stream.of(attributes)
                     .map(attribute -> root.get(attribute))
                     .collect(Collectors.toUnmodifiableList());
    }

}
