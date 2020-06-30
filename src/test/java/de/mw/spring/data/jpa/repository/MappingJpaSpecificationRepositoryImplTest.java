package de.mw.spring.data.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked","rawtypes"})
class MappingJpaSpecificationRepositoryImplTest extends AbstractRepositoryQueryTest {
    
    @Mock
    private TypedQuery query;
    
    @Mock
    private Projection projection;
    
    @Mock
    private Stream resultStream;
    
    @Mock
    private Specification specification;
    
    @Mock
    private Sort sort;
    
    @Mock
    private ProjectionMapper mapper;
    
    private MappingJpaSpecificationRepositoryImpl<TestEntity,Long> testee;
    
    @Entity
    private static class TestEntity {
        
        @Id
        Long id;
        
    }
    
    @BeforeEach
    void setup() {
        testee = spy(new MappingJpaSpecificationRepositoryImpl<>(TestEntity.class, entityManagerSpy));
    }
    
    
    @Test
    void findOne_withType_callsProjectingFindOne_andMapsResultWithMapper() {
        var mockResult = Optional.of(4711L);
        doReturn(mockResult).when(testee).findOne(any(), any(), any(), any(Projection.class));
        doReturn(42L).when(mapper).toModel(any());
        
        var result = testee.findOne(specification, sort, Long.class, projection, mapper);
        
        verify(testee).findOne(specification, sort, Long.class, projection);
        verify(mapper).toModel(4711L);
        assertThat(result).isEqualTo(Optional.of(42L));
    }
    
    @Test
    void findOne_withoutType_callsProjectingFindOneWithTupleType_andMapsResultWithMapper() {
        var mockTuple = mock(Tuple.class);
        var mockResult = Optional.of(mockTuple);
        doReturn(mockResult).when(testee).findOne(any(), any(), any(), any(Projection.class));
        doReturn(42L).when(mapper).toModel(any());
        
        var result = testee.findOne(specification, sort, projection, mapper);
        
        verify(testee).findOne(specification, sort, Tuple.class, projection);
        verify(mapper).toModel(mockTuple);
        assertThat(result).isEqualTo(Optional.of(42L));
    }
    
    @Test
    void findAllStream_withType_callsProjectingFindAllStream_andMapsResultWithMapper() {
        var mockHints = mock(Map.class);
        var mockResult = Stream.of(4711L);
        doReturn(mockResult).when(testee).findAllStream(any(), any(), any(), any(), any(Projection.class));
        doReturn(42L).when(mapper).toModel(any());
        
        var result = testee.findAllStream(specification, sort, mockHints, Long.class, projection, mapper);
        
        assertThat(result).containsExactly(42L);
        verify(testee).findAllStream(specification, sort, mockHints, Long.class, projection);
        verify(mapper).toModel(4711L);
    }
    
    @Test
    void findAllStream_withoutType_callsProjectingFindAllStreamWithTupleType_andMapsResultWithMapper() {
        var mockHints = mock(Map.class);
        var mockTuple = mock(Tuple.class);
        var mockResult = Stream.of(mockTuple);
        doReturn(mockResult).when(testee).findAllStream(any(), any(), any(), any(), any(Projection.class));
        doReturn(42L).when(mapper).toModel(any());
        
        var result = testee.findAllStream(specification, sort, mockHints, projection, mapper);
        
        assertThat(result).containsExactly(42L);
        verify(testee).findAllStream(specification, sort, mockHints, Tuple.class, projection);
        verify(mapper).toModel(mockTuple);
    }
    
}
