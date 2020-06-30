package de.mw.spring.data.jpa.repository.projection;

import javax.persistence.Tuple;

import java.lang.reflect.Constructor;

import de.mw.spring.data.jpa.repository.ProjectionMapper;
import lombok.SneakyThrows;

/**
 * Simple projection mapper strategy by constructor invocation with tuple values.
 * 
 * If the constructor does not match the tuple structure it will fail at runtime.
 */
public class ProjectionMapperByConstructor<R> implements ProjectionMapper<Tuple,R> {

    protected Constructor<R> constructor;
    
    
    public ProjectionMapperByConstructor(Constructor<R> constructor) {
        this.constructor = constructor;
    }
    
    @SneakyThrows
    protected R newInstance(Tuple tuple) {
        return constructor.newInstance(tuple.toArray());
    }
    
    @Override
    public R toModel(Tuple tuple) {
        return newInstance(tuple);
    }
    
}
