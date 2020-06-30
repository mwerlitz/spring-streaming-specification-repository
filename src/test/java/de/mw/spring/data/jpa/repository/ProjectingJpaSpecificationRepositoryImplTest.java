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
import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked","rawtypes"})
class ProjectingJpaSpecificationRepositoryImplTest extends AbstractRepositoryQueryTest {
    
    @Mock
    private TypedQuery query;
    
    @Mock
    private Projection projection;
    
    @Mock
    private Stream resultStream;
    
    private Specification testSpecification = (root, query, builder) -> builder.equal(root.get("id"), 42L);
    
    private Sort testSort = Sort.by("id");
    
    private ProjectingJpaSpecificationRepositoryImpl<TestEntity,Long> testee;
    
    @Entity
    private static class TestEntity {
        
        @Id
        Long id;
        
    }
    
    @Override
    protected TypedQuery configureMock_createQuery_CriteriaQuery(TypedQuery mock) {
        return query;
    }
    
    @BeforeEach
    void setup() {
        testee = spy(new ProjectingJpaSpecificationRepositoryImpl<>(TestEntity.class, entityManagerSpy));
    }
    
    
    @Test
    void findOne_withType_createsProjectionQuery_andReturnsSingleResult() {
        doReturn(query).when(testee).createProjectionQuery(any(), any(), any(), any());
        when(query.getSingleResult()).thenReturn(4711L);
        
        var result = testee.findOne(testSpecification, testSort, Long.class, projection);
        
        assertThat(result).isEqualTo(Optional.of(4711L));
        verify(testee).createProjectionQuery(testSpecification, testSort, Long.class, projection);
    }
    
    @Test
    void findOne_withType_returnsEmptyOptional_onNoResultException() {
        doReturn(query).when(testee).createProjectionQuery(any(), any(), any(), any());
        when(query.getSingleResult()).thenThrow(NoResultException.class);
        
        var result = testee.findOne(testSpecification, testSort, Long.class, projection);
        
        assertThat(result).isEqualTo(Optional.empty());
    }
    
    @Test
    void findOne_withoutType_createsTupleProjectionQuery_andReturnsSingleResult() {
        doReturn(query).when(testee).createProjectionQuery(any(), any(), any(), any());
        when(query.getSingleResult()).thenReturn(4711L);
        
        var result = testee.findOne(testSpecification, testSort, projection);
        
        assertThat(result).isEqualTo(Optional.of(4711L));
        verify(testee).createProjectionQuery(testSpecification, testSort, Tuple.class, projection);
    }
    
    @Test
    void findOne_withoutType_returnsEmptyOptional_onNoResultException() {
        doReturn(query).when(testee).createProjectionQuery(any(), any(), any(), any());
        when(query.getSingleResult()).thenThrow(NoResultException.class);
        
        var result = testee.findOne(testSpecification, testSort, projection);
        
        assertThat(result).isEqualTo(Optional.empty());
    }
    
    @Test
    void findAllStream_withType_createsProjectionQuery_appliesHints_returnsResultStream() throws Exception {
        Map<String, Object> queryHints = Map.of("foo", "bar");
        doReturn(query).when(testee).createProjectionQuery(any(), any(), any(), any());
        when(query.getResultStream()).thenReturn(resultStream);
        
        var result = testee.findAllStream(testSpecification, testSort, queryHints, Long.class, projection);
        
        assertThat(result).isEqualTo(resultStream);
        verify(testee).createProjectionQuery(testSpecification, testSort, Long.class, projection);
        verify(query).getResultStream();
        verify(query).setHint("foo", "bar");
    }
    
    @Test
    void findAllStream_withoutType_createsTupleProjectionQuery_appliesHints_returnsResultStream() throws Exception {
        Map<String, Object> queryHints = Map.of("foo", "bar");
        doReturn(query).when(testee).createProjectionQuery(any(), any(), any(), any());
        when(query.getResultStream()).thenReturn(resultStream);
        
        var result = testee.findAllStream(testSpecification, testSort, queryHints, projection);
        
        assertThat(result).isEqualTo(resultStream);
        verify(testee).createProjectionQuery(testSpecification, testSort, Tuple.class, projection);
        verify(query).getResultStream();
        verify(query).setHint("foo", "bar");
    }

    @Test
    void createProjectionQuery_createsCorrectQueryWithProjection() throws Exception {
        Projection testProjection = (root, query, builder) -> List.of(root.get("id"));
        
        var result = testee.createProjectionQuery(testSpecification, testSort, Tuple.class, testProjection);
        
        assertThat(result).isEqualTo(query);
        assertThat(getCiteriaQuery().getResultType()).isEqualTo(Tuple.class);
        assertThat(getQueryString("test")).containsSubsequence("select test.id from", "where test.id=42L", "order by test.id");
    }

}
