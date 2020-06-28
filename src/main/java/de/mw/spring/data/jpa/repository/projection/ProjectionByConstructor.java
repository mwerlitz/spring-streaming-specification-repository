package de.mw.spring.data.jpa.repository.projection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

/**
 * Simple projection strategy by given construtor parameter names.
 * 
 * There is no check for the availability of the attributes.
 * So the produced query may fail at runtime.
 */
public class ProjectionByConstructor<T,R> extends ProjectionByAttributeNames<T> {
    
    public ProjectionByConstructor(Constructor<R> constructor) {
        this.attributes = findAttributes(constructor);
    }
    
    protected String[] findAttributes(Constructor<R> constructor) {
        if (constructor.getParameterCount() == 0) {
            throw new IllegalArgumentException("Constructor of type " + constructor.getDeclaringClass() + " has no parameters to project");
        }
        
        return Stream.of(constructor.getParameters())
                     .map(Parameter::getName)
                     .toArray(String[]::new);
    }
    
}
