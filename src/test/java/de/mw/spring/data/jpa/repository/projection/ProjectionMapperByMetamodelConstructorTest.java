package de.mw.spring.data.jpa.repository.projection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import javax.persistence.Tuple;
import javax.persistence.metamodel.Attribute;

class ProjectionMapperByMetamodelConstructorTest {
   
    private static class TestClass {
        
        private final boolean constructorInvoked;
        private final String string;
        
        
        @SuppressWarnings("unused")
        public TestClass(String string) {
            this.string = string;
            this.constructorInvoked = true;
        }
        
        @SuppressWarnings("unused")
        public TestClass() {
            this.string = "no no no";
            this.constructorInvoked = false;
            fail();
        }
        
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    void toModel_invokesMatchingConstructorByAttributeTypesWithTupleValues() throws Exception {
        var attr = mock(Attribute.class);
        when(attr.getJavaType()).thenReturn(String.class);
        var tuple = mock(Tuple.class);
        var tupleValues = new Object[] {"foo"};
        when(tuple.toArray()).thenReturn(tupleValues);
        var testee = new ProjectionMapperByMetamodelConstructor(TestClass.class, attr);
        
        
        var result = (TestClass) testee.toModel(tuple);
        
        
        assertThat(result.constructorInvoked).isTrue();
        assertThat(result.string).isEqualTo("foo");
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    void constructor_throwsException_whenNoMatchingConstructorByAttributeTypesCouldBeFound() throws Exception {
        var attr = mock(Attribute.class);
        when(attr.getJavaType()).thenReturn(int.class);
        
        
        assertThrows(IllegalStateException.class, () -> new ProjectionMapperByMetamodelConstructor(TestClass.class, attr));
    }

}
