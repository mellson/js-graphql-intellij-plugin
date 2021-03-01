package com.intellij.lang.jsgraphql.types.execution;

import com.intellij.lang.jsgraphql.types.GraphQLException;
import com.intellij.lang.jsgraphql.types.PublicApi;
import com.intellij.lang.jsgraphql.types.schema.GraphQLNamedOutputType;
import com.intellij.lang.jsgraphql.types.schema.GraphQLType;
import com.intellij.lang.jsgraphql.types.schema.GraphQLTypeUtil;

/**
 * This is thrown if a {@link graphql.schema.TypeResolver} fails to give back a concrete type
 * or provides a type that doesn't implement the given interface or union.
 */
@PublicApi
public class UnresolvedTypeException extends GraphQLException {

    private final GraphQLNamedOutputType interfaceOrUnionType;

    /**
     * Constructor to use a custom error message
     * for an error that happened during type resolution.
     *
     * @param message              custom error message.
     * @param interfaceOrUnionType expected type.
     */
    public UnresolvedTypeException(String message, GraphQLNamedOutputType interfaceOrUnionType) {
        super(message);
        this.interfaceOrUnionType = interfaceOrUnionType;
    }

    public UnresolvedTypeException(GraphQLNamedOutputType interfaceOrUnionType) {
        this("Could not determine the exact type of '" + interfaceOrUnionType.getName() + "'", interfaceOrUnionType);
    }

    public UnresolvedTypeException(GraphQLNamedOutputType interfaceOrUnionType, GraphQLType providedType) {
        this("Runtime Object type '" + GraphQLTypeUtil.simplePrint(providedType) + "' is not a possible type for "
                + "'" + interfaceOrUnionType.getName() + "'.", interfaceOrUnionType);
    }

    public GraphQLNamedOutputType getInterfaceOrUnionType() {
        return interfaceOrUnionType;
    }

}
