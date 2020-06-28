package de.mw.spring.data.jpa.repository.projection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.stream.Stream;

import de.mw.spring.data.jpa.repository.Projection;
import de.mw.spring.data.jpa.repository.ProjectionMapper;
import lombok.experimental.Delegate;

/**
 * Utility projectionConstructor class combining a {@link ProjectionConstructor} by using the parameter names of a preferred constructor of the 
 * target mapping class with the matching {@link ProjectionMapper} using preferred constructor.
 * 
 * If there are multiple parameterized constructors the preferred constructor should be annotated by {@link ProjectionConstructor} and possibly qualified.
 *
 * @param <T> type of entity
 * @param <R> type of tuple result mapping
 */
public class ConstructorProjectionUtil<T, R> implements Projection<T>, ProjectionMapper<R> {

    @Delegate
    private Projection<T> projection;
    @Delegate
    private ProjectionMapper<R> projectionMapper;
    
    /**
     * @param clazz class to project to
     */
    public ConstructorProjectionUtil(Class<R> clazz) {
        this(clazz, null);
    }
    
    /**
     * @param clazz clazz class to project to
     * @param projectionConstructor qualifier for value of {@link ProjectionConstructor}
     */
    public ConstructorProjectionUtil(Class<R> clazz, @Nullable String projectionConstructor) {
        Constructor<R> preferredConstructor = findConstructor(clazz, projectionConstructor);
        this.projection = new ProjectionByConstructor<>(preferredConstructor);
        this.projectionMapper = new ProjectionMapperByConstructor<>(preferredConstructor);
    }
            
    @SuppressWarnings("unchecked")
    protected Constructor<R> findConstructor(Class<R> clazz, @Nullable String projection) {
        Constructor<R>[] constructors = Stream.of(clazz.getConstructors())
                                              .filter(con -> con.getParameterCount() > 0) // no-args is useless
                                              .filter(con -> {
                                                  if (projection != null) { // filter by qualifier
                                                      return con.isAnnotationPresent(ProjectionConstructor.class) && 
                                                             StringUtils.equals(con.getAnnotation(ProjectionConstructor.class).value(), projection);
                                                  }
                                                  return true;
                                              })
                                              .toArray(Constructor[]::new);
        
        if (constructors.length == 0) {
            throw new IllegalArgumentException("Could not find a parameterized constructor" + (projection != null ? " matching projectionConstructor value " + projection : ""));
        }
        
        // prefer constructor with @ProjectionConstructor
        if (constructors.length > 1 && Stream.of(constructors).anyMatch(con -> con.isAnnotationPresent(ProjectionConstructor.class))) {
            constructors = Stream.of(constructors)
                                 .filter(con -> con.isAnnotationPresent(ProjectionConstructor.class))
                                 .toArray(Constructor[]::new);
        }
        
        // which one to choose?
        if (constructors.length > 1) {
            throw new IllegalArgumentException("Found multiple parameterized constructors, you might want to declare a qualified one with @ProjectionConstructor");
        }
        
        return constructors[0];
    }
    
    /**
     * Marker for constructors used for (qualified) projection
     */
    @Target({ElementType.CONSTRUCTOR})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface ProjectionConstructor {

        /**
         * Qualifier for different projections
         */
        String value() default "";

    }
}
