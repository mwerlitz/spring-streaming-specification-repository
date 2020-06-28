package de.mw.spring.data.jpa.repository.projection;

import javax.persistence.metamodel.Attribute;

import de.mw.spring.data.jpa.repository.Projection;
import de.mw.spring.data.jpa.repository.ProjectionMapper;
import lombok.experimental.Delegate;

/**
 * Utility projection class combining a {@link Projection} by metamodel attribues
 * with the matching {@link ProjectionMapper} using constructor mapping by the metamodel attribues.
 *
 * @param <T> type of entity
 * @param <R> type of tuple result mapping
 */
public class MetamodelProjectionUtil<T,R> implements Projection<T>, ProjectionMapper<R> {

    @Delegate
    private Projection<T> projection;
    @Delegate
    private ProjectionMapper<R> projectionMapper;
    
    @SafeVarargs
    public MetamodelProjectionUtil(Class<R> clazz, Attribute<T,?>... attributes) {
        this.projection = new ProjectionByMetamodelAttributes<>(attributes);
        this.projectionMapper = new ProjectionMapperByMetamodelConstructor<>(clazz, attributes);
    }

}
