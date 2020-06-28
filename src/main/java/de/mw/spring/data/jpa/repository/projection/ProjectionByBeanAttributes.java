package de.mw.spring.data.jpa.repository.projection;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.stream.Stream;

/**
 * Simple projection strategy by writable bean attribute names.
 * 
 * There is no check for the availability of the attributes.
 * So the produced query may fail at runtime.
 */
public class ProjectionByBeanAttributes<T,R> extends ProjectionByAttributeNames<T> {
    
    public ProjectionByBeanAttributes(Class<R> clazz) {
        this.attributes = getBeanParameterNames(clazz);
    }

    protected String[] getBeanParameterNames(Class<R> clazz) {
        return Stream.of(BeanUtils.getPropertyDescriptors(clazz))
                     .filter(descr -> descr.getWriteMethod() != null)
                     .map(PropertyDescriptor::getName)
                     .toArray(String[]::new);
    }
}
