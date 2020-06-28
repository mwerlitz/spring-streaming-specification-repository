package de.mw.spring.data.jpa.repository.projection;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.model.PreferredConstructorDiscoverer;

import java.lang.reflect.Constructor;

import de.mw.spring.data.jpa.repository.Projection;
import de.mw.spring.data.jpa.repository.ProjectionMapper;
import lombok.experimental.Delegate;

/**
 * Utility projection class combining a {@link Projection} by using the parameter names of a preferred constructor of the 
 * target mapping class with the matching {@link ProjectionMapper} using preferred constructor.
 * 
 * If there are multiple parameterized constructors the preferred constructor should be annotated by {@link PersistenceConstructor}.
 *
 * @param <T> type of entity
 * @param <R> type of tuple result mapping
 */
public class PreferredConstructorProjectionUtil<T, R> implements Projection<T>, ProjectionMapper<R> {

    @Delegate
    private Projection<T> projection;
    @Delegate
    private ProjectionMapper<R> projectionMapper;
    
    public PreferredConstructorProjectionUtil(Class<R> clazz) {
        Constructor<R> preferredConstructor = findConstructor(clazz);
        this.projection = new ProjectionByConstructor<>(preferredConstructor);
        this.projectionMapper = new ProjectionMapperByConstructor<>(preferredConstructor);
    }
            
    protected Constructor<R> findConstructor(Class<R> clazz) {
        PreferredConstructor<R, ?> preferredConstructor = PreferredConstructorDiscoverer.discover(clazz);
        if (preferredConstructor == null || preferredConstructor.isNoArgConstructor()) {
            throw new IllegalArgumentException("Could not find parameterized constructor, you might declare one with @PersistenceConstructor");
        }
        return preferredConstructor.getConstructor();
    }
    
}
