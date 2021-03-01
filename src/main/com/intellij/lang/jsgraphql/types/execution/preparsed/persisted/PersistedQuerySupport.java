package com.intellij.lang.jsgraphql.types.execution.preparsed.persisted;

import com.intellij.lang.jsgraphql.types.ExecutionInput;
import com.intellij.lang.jsgraphql.types.GraphQLError;
import com.intellij.lang.jsgraphql.types.GraphqlErrorBuilder;
import com.intellij.lang.jsgraphql.types.PublicSpi;
import com.intellij.lang.jsgraphql.types.execution.preparsed.PreparsedDocumentEntry;
import com.intellij.lang.jsgraphql.types.execution.preparsed.PreparsedDocumentProvider;

import java.util.Optional;
import java.util.function.Function;

import static com.intellij.lang.jsgraphql.types.Assert.assertNotNull;

/**
 * This abstract class forms the basis for persistent query support.  Derived classes
 * need to implement the method to work out the query id and you also need
 * a {@link PersistedQueryCache} implementation.
 *
 * @see graphql.execution.preparsed.PreparsedDocumentProvider
 * @see graphql.GraphQL.Builder#preparsedDocumentProvider(graphql.execution.preparsed.PreparsedDocumentProvider)
 */
@PublicSpi
public abstract class PersistedQuerySupport implements PreparsedDocumentProvider {

    /**
     * In order for {@link ExecutionInput#getQuery()} to never be null, use this to mark
     * them so that invariant can be satisfied while assuming that the persisted query id is elsewhere
     */
    public static final String PERSISTED_QUERY_MARKER = "PersistedQueryMarker";

    private final PersistedQueryCache persistedQueryCache;

    public PersistedQuerySupport(PersistedQueryCache persistedQueryCache) {
        this.persistedQueryCache = assertNotNull(persistedQueryCache);
    }

    @Override
    public PreparsedDocumentEntry getDocument(ExecutionInput executionInput, Function<ExecutionInput, PreparsedDocumentEntry> parseAndValidateFunction) {
        Optional<Object> queryIdOption = getPersistedQueryId(executionInput);
        assertNotNull(queryIdOption, () -> String.format("The class %s MUST return a non null optional query id", this.getClass().getName()));

        try {
            if (queryIdOption.isPresent()) {
                Object persistedQueryId = queryIdOption.get();
                return persistedQueryCache.getPersistedQueryDocument(persistedQueryId, executionInput, (queryText) -> {
                    // we have a miss and they gave us nothing - bah!
                    if (queryText == null || queryText.trim().length() == 0) {
                        throw new PersistedQueryNotFound(persistedQueryId);
                    }
                    ExecutionInput newEI = executionInput.transform(builder -> builder.query(queryText));
                    return parseAndValidateFunction.apply(newEI);
                });
            }
            // ok there is no query id - we assume the query is indeed ready to go as is - ie its not a persisted query
            return parseAndValidateFunction.apply(executionInput);
        } catch (PersistedQueryNotFound e) {
            return mkMissingError(e);
        }
    }

    /**
     * This method is required for concrete types to work out the query id (often a hash) that should be used to look
     * up the persisted query in the cache.
     *
     * @param executionInput the execution input
     * @return an optional id of the persisted query
     */
    abstract protected Optional<Object> getPersistedQueryId(ExecutionInput executionInput);

    /**
     * Allows you to customize the graphql error that is sent back on a missing persistend query
     *
     * @param persistedQueryNotFound the missing persistent query exception
     * @return a PreparsedDocumentEntry that holds an error
     */
    protected PreparsedDocumentEntry mkMissingError(PersistedQueryNotFound persistedQueryNotFound) {
        GraphQLError gqlError = GraphqlErrorBuilder.newError()
                .errorType(persistedQueryNotFound).message(persistedQueryNotFound.getMessage())
                .extensions(persistedQueryNotFound.getExtensions()).build();
        return new PreparsedDocumentEntry(gqlError);
    }
}
