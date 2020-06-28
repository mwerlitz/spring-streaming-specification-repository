package de.mw.spring.data.jpa.repository.projection;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import javax.persistence.metamodel.Attribute;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Simple projection mapper strategy by constructor invocation with tuple values.
 * 
 * The contructor is determined a creation time, so the mapping cannot fail at runtime.
 */
public class ProjectionMapperByMetamodelConstructor<T,R> extends ProjectionMapperByConstructor<R> {
    
    @SafeVarargs
    public ProjectionMapperByMetamodelConstructor(Class<R> clazz, Attribute<T,?>... attributes) {
        super(null);
        this.constructor = findConstructor(clazz, attributes);
    }
    
    protected Constructor<R> findConstructor(Class<R> clazz, Attribute<T,?>[] attributes) {
        Class<?>[] parameters = Stream.of(attributes).map(Attribute::getJavaType).toArray(Class<?>[]::new);
        Constructor<R> constructor = ConstructorUtils.getMatchingAccessibleConstructor(clazz, parameters);
        
        if (constructor == null) {
            throw new IllegalStateException("Could not find accessible constructor on " + clazz + " for parameters " + Arrays.toString(parameters));
        }
        return constructor;
    }
    
}
