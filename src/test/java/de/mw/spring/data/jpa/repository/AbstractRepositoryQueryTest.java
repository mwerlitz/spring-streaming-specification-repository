package de.mw.spring.data.jpa.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.AdditionalAnswers;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.repository.Repository;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Base class for component testing the criteria query of repositories.
 * 
 * The basic idea is to setup a context with a in-memory database but the real target hibernate dialect.
 * So the JPA metadata and persistence context are setup.
 * 
 * Then to setup a spy EntityManager that does not really execute the queries against the database and returns mock queries.
 * Instead the real to be executed query is stored for verification.
 * 
 * Futhermore with the help of the {@link #getQueryString(String)} method the real query is transformed to a HQL query
 * that can be asserted for logical correctness of the created criteria query.
 */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.properties.hibernate.dialect=de.mw.spring.data.jpa.repository.AbstractRepositoryQueryTest$TestingOrcale12cDialect"
})
public abstract class AbstractRepositoryQueryTest {

    @Autowired
    protected EntityManager entityManager;
    protected EntityManager entityManagerSpy;
    
    protected List<AbstractQuery<?>> criteriaQuerys; // list of created criteria queries
    protected List<TypedQuery<?>>    querys;         // list of created queries
    protected List<TypedQuery<?>>    queryMocks;     // list of mock response for created queries


    @SuppressWarnings("unchecked")
    @BeforeEach
    void abstractSetup() {
        entityManagerSpy = mock(EntityManager.class, AdditionalAnswers.delegatesTo(entityManager)); // simple spy() of spring proxy does not work
        criteriaQuerys = new ArrayList<>();
        querys = new ArrayList<>();
        queryMocks = new ArrayList<>();
        
        // intercept call to create query, so we have a chance to spy it (in order to prevent a real DB query)
        doAnswer(answerCreateQuery(this::configureMock_createQuery_CriteriaQuery) ).when(entityManagerSpy).createQuery(any(CriteriaQuery.class));
        doAnswer(answerCreateQuery(this::configureMock_createQuery_CriteriaUpdate)).when(entityManagerSpy).createQuery(any(CriteriaUpdate.class));
        doAnswer(answerCreateQuery(this::configureMock_createQuery_CriteriaDelete)).when(entityManagerSpy).createQuery(any(CriteriaDelete.class));
        doNothing().when(entityManagerSpy).flush();
    }

    /**
     * Answer for {@link EntityManager#createQuery(CriteriaQuery)},
     *            {@link EntityManager#createQuery(CriteriaUpdate)} and
     *            {@link EntityManager#createQuery(CriteriaDelete)}
     *            
     * @param mockConfigurer function to configure a new mock query
     * @return mock query
     */
    protected Answer<TypedQuery<?>> answerCreateQuery(Function<TypedQuery<?>, TypedQuery<?>> mockConfigurer) {
        return (invocation) -> {
            TypedQuery<?> q = (TypedQuery<?>) invocation.getMethod().invoke(entityManager, invocation.getArguments());
            
            criteriaQuerys.add(invocation.getArgument(0));
            querys.add(q);
            
            if (queryMocks.size() >= querys.size()) {
                return queryMocks.get(querys.size() - 1); // return prepared mock query
            } else {
                TypedQuery<?> mock = mock(TypedQuery.class); // create and return a new mock for query
                queryMocks.add(mock);
                return mockConfigurer.apply(mock);
            }
        };
    }
    
    /**
     * Configures a mock for usage with {@link EntityManager#createQuery(CriteriaQuery)}
     */
    protected TypedQuery<?> configureMock_createQuery_CriteriaQuery(TypedQuery<?> mock) {
        doReturn(Stream.of()).when(mock).getResultStream();
        doReturn(List.of()).when(mock).getResultList();
        return mock;
    }
    
    /**
     * Configures a mock for usage with {@link EntityManager#createQuery(CriteriaUpdate)}
     */
    protected TypedQuery<?> configureMock_createQuery_CriteriaUpdate(TypedQuery<?> mock) {
        doReturn(1).when(mock).executeUpdate();
        return mock;
    }
    
    /**
     * Configures a mock for usage with {@link EntityManager#createQuery(CriteriaDelete)}
     */
    protected TypedQuery<?> configureMock_createQuery_CriteriaDelete(TypedQuery<?> mock) {
        doReturn(1).when(mock).executeUpdate();
        return mock;
    }
    
    /**
     * Creates a spring data jpa repository
     * @param <R>
     * @param <T>
     * @param repositoryClass
     * @param entityClass
     * @return
     */
    protected <R extends Repository<T, ?>, T> R createJpaRepositoryWithSpyEntityManager(Class<R> repositoryClass, Class<T> entityClass) {
        // IMPORTANT: the repository impl class should match the one configured at @EnableJpaRepositories
        return mock(repositoryClass, AdditionalAnswers.delegatesTo(new MappingJpaSpecificationRepositoryImpl<>(entityClass, entityManagerSpy)));
    }
    
    /**
     * Translates a query to HQL with replaced parameters (toString)
     * 
     * @param query the query
     * @param alias an alias for the root entity in HQL string
     */
    protected String getQueryString(Query query, String alias) {
        var hibernateQuery = query.unwrap(org.hibernate.query.Query.class);
        String hql = hibernateQuery.getQueryString();
        
        String queryString = hql.replace("generatedAlias0", alias);
        for (var param : hibernateQuery.getParameterMetadata().collectAllParameters()) {
            queryString = queryString.replace(":" + param.getName(), hibernateQuery.getParameterValue(param).toString());
        }
        
        return queryString;
    }
    
    protected TypedQuery<?> getQueryMock() {
        return queryMocks.get(0);
    }
    
    protected TypedQuery<?> getQueryMock(int i) {
        return queryMocks.get(i);
    }
    
    protected TypedQuery<?> getQuery() {
        return querys.get(0);
    }
    
    protected TypedQuery<?> getQuery(int i) {
        return querys.get(i);
    }
    
    protected AbstractQuery<?> getCiteriaQuery() {
        return criteriaQuerys.get(0);
    }
    
    protected AbstractQuery<?> getCiteriaQuery(int i) {
        return criteriaQuerys.get(i);
    }
    
    protected String getQueryString(String alias) {
        return getQueryString(getQuery(), alias);
    }
    
    protected String getQueryString(int i, String alias) {
        return getQueryString(getQuery(i), alias);
    }
    
    protected void addQueryMock(TypedQuery<?> q) {
        queryMocks.add(q);
    }
     
    /**
     * Prevent hibernate from loading of sequence information that does not exist
     */
    public static class TestingOrcale12cDialect extends Oracle12cDialect {

        @Override
        public SequenceInformationExtractor getSequenceInformationExtractor() {
            return SequenceInformationExtractorNoOpImpl.INSTANCE;
        }
        
    }
}
