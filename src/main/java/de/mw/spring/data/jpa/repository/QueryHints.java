package de.mw.spring.data.jpa.repository;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Helper class for creating QueryHints suitable for streaming JPA queries
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryHints {

    /**
     * Creates a set of query hints for hibernate suitable for streaming JPA queries
     * with the given fetch size.
     * 
     * @param fetchSize query fetch size
     */
    public static Map<String, Object> streamingQueryHints(int fetchSize) {
        Map<String, Object> queryHints = new HashMap<>();
        queryHints.put(org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE, String.valueOf(fetchSize));
        queryHints.put(org.hibernate.jpa.QueryHints.HINT_CACHEABLE, "false");
        queryHints.put(org.hibernate.jpa.QueryHints.HINT_READONLY,  "true");
        
        return queryHints;
    }

}
