package de.mw.spring.data.jpa.repository.projection;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.mw.spring.data.jpa.repository.Projection;

/**
 * Simple projection strategy by given JPA entity metamodel attributes.
 * 
 * Using the metamodel the mapping should be safe at runtime.
 */
public class ProjectionByMetamodelAttributes<T> implements Projection<T,Tuple> {

    private Attribute<T,?>[] attributes;
    
    @SafeVarargs
    public ProjectionByMetamodelAttributes(Attribute<T,?>... attributes) {
        this.attributes = attributes;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Selection<?>> toSelections(Root<T> root, CriteriaQuery<Tuple> query, CriteriaBuilder criteriaBuilder) {
        return Stream.of(attributes)
                     .map(attribute -> {
                         if (attribute instanceof SingularAttribute) {
                             return root.get((SingularAttribute<T,?>) attribute);
                         } else if (attribute instanceof PluralAttribute) {
                             return root.get((PluralAttribute<T, Collection<Object>, Object>) attribute);
                         } else {
                             return root.get(attribute.getName());
                         }
                     })
                     .collect(Collectors.toUnmodifiableList());
    }

}
