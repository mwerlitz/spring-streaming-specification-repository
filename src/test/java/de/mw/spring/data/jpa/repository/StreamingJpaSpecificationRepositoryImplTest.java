package de.mw.spring.data.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.TypedQuery;

import java.util.Map;
import java.util.stream.Stream;

class StreamingJpaSpecificationRepositoryImplTest extends AbstractRepositoryQueryTest {
    
    @Mock
    private TypedQuery<TestEntity> query;
    
    @Mock
    private Stream<TestEntity> resultStream;
    
    private Specification<TestEntity> testSpecification = (root, query, builder) -> builder.equal(root.get("id"), 42L);
    
    private Sort testSort = Sort.by("id");
    
    private StreamingJpaSpecificationRepositoryImpl<TestEntity,Long> testee;
    
    @Entity
    private static class TestEntity {
        
        @Id
        Long id;
        
    }
    
    @Override
    protected TypedQuery<?> configureMock_createQuery_CriteriaQuery(TypedQuery<?> mock) {
        return query;
    }
    
    @BeforeEach
    void setup() {
        testee = spy(new StreamingJpaSpecificationRepositoryImpl<>(TestEntity.class, entityManagerSpy));
    }
    
    @Test
    void findAllStream_createsQuery_appliesHints_returnsResultStream() throws Exception {
        Map<String, Object> queryHints = Map.of("foo", "bar");
        when(query.getResultStream()).thenReturn(resultStream);
        
        var result = testee.findAllStream(testSpecification, testSort, queryHints);
        
        assertThat(result).isEqualTo(resultStream);
        assertThat(getQueryString("test")).containsSubsequence("where test.id=42L", "order by test.id");
        verify(query).getResultStream();
        verify(query).setHint("foo", "bar");
    }

}
