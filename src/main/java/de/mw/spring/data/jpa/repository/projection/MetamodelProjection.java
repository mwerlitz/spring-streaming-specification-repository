package de.mw.spring.data.jpa.repository.projection;

import javax.persistence.Tuple;
import javax.persistence.metamodel.Attribute;

import de.mw.spring.data.jpa.repository.Projection;
import de.mw.spring.data.jpa.repository.ProjectionMapper;
import de.mw.spring.data.jpa.repository.projection.ConstructorProjection.ProjectionConstructor;
import lombok.experimental.Delegate;

/**
 * Utility projection class combining a {@link ProjectionConstructor} by metamodel attribues
 * with the matching {@link ProjectionMapper} using constructor mapping by the metamodel attribues.
 *
 * @param <T> type of entity
 * @param <R> type of tuple result mapping
 */
public class MetamodelProjection<T,R> implements Projection<T,Tuple>, ProjectionMapper<Tuple,R> {

    @Delegate
    private Projection<T,Tuple> projection;
    @Delegate
    private ProjectionMapper<Tuple,R> projectionMapper;
    
    @SafeVarargs
    public MetamodelProjection(Class<R> clazz, Attribute<T,?>... attributes) {
        this.projection = new ProjectionByMetamodelAttributes<>(attributes);
        this.projectionMapper = new ProjectionMapperByMetamodelConstructor<>(clazz, attributes);
    }

}
