package de.mw.spring.data.jpa.repository.projection;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;

import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * Simple dynamic projection mapper strategy by constructor invocation with tuple values.
 * 
 * The constructor is searched once at runtime with the tuple element types of the first processd tuple.
 * It is assumed that all tuples will have the same structure. Otherwise mapping will fail at runtime.
 * If the is no constructor matching the tuple structure it will fail at runtime.
 */
public class ProjectionMapperByTupleElementsConstructor<R> extends ProjectionMapperByConstructor<R> {

    protected Class<R> clazz;
    
    public ProjectionMapperByTupleElementsConstructor(Class<R> clazz) {
        super(null);
        this.clazz = clazz;
    }
    
    @Override
    public R toModel(Tuple tuple) {
        if (constructor == null) {
            constructor = findConstructorByTupleSignature(tuple, clazz);
        }
        
        return newInstance(tuple);
    }
    
    protected Constructor<R> findConstructorByTupleSignature(Tuple tuple, Class<R> clazz) {
        Class<?>[] parameters = tuple.getElements().stream().map(TupleElement::getJavaType).toArray(Class<?>[]::new);
        Constructor<R> constructor = ConstructorUtils.getMatchingAccessibleConstructor(clazz, parameters);
        
        if (constructor == null) {
            throw new IllegalStateException("Could not find accessible constructor on " + clazz + " for parameters " + Arrays.toString(parameters));
        }
        return constructor;
    }
    
}
