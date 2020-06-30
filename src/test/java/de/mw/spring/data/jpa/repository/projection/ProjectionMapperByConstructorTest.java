package de.mw.spring.data.jpa.repository.projection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import javax.persistence.Tuple;

class ProjectionMapperByConstructorTest {
   
    private static class TestClass {
        
        private final boolean constructorInvoked;
        private final String string;
        private final int integer;
        
        
        @SuppressWarnings("unused")
        public TestClass(String string, int integer) {
            this.string = string;
            this.integer = integer;
            
            this.constructorInvoked = true;
        }
        
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    void toModel_invokesConfiguredConstructorWithTupleValues() throws Exception {
        var tuple = mock(Tuple.class);
        var tupleValues = new Object[] {"foo", 42};
        when(tuple.toArray()).thenReturn(tupleValues);
        var testee = new ProjectionMapperByConstructor(TestClass.class.getConstructors()[0]);
        
        
        var result = (TestClass) testee.toModel(tuple);
        
        
        assertThat(result.constructorInvoked).isTrue();
        assertThat(result.string).isEqualTo("foo");
        assertThat(result.integer).isEqualTo(42);
    }

}
