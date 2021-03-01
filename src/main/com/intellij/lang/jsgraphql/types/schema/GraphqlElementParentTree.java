package com.intellij.lang.jsgraphql.types.schema;

import com.intellij.lang.jsgraphql.types.Internal;
import com.intellij.lang.jsgraphql.types.PublicApi;

import java.util.*;

import static com.intellij.lang.jsgraphql.types.Assert.assertNotNull;
import static com.intellij.lang.jsgraphql.types.Assert.assertTrue;

/**
 * This represents a hierarchy an graphql runtime element upwards to its
 * associated parent elements.  For example a GraphqlDirective can be on a GraphqlArgument
 * which can be on a GraphqlFieldDefinition, which can be on a GraphqlObjectType.
 */
@PublicApi
public class GraphqlElementParentTree {

    private final GraphQLSchemaElement element;
    private final GraphqlElementParentTree parent;

    @Internal
    public GraphqlElementParentTree(Deque<GraphQLSchemaElement> nodeStack) {
        assertNotNull(nodeStack, () -> "You MUST have a non null stack of elements");
        assertTrue(!nodeStack.isEmpty(), () -> "You MUST have a non empty stack of element");

        Deque<GraphQLSchemaElement> copy = new ArrayDeque<>(nodeStack);
        element = copy.pop();
        if (!copy.isEmpty()) {
            parent = new GraphqlElementParentTree(copy);
        } else {
            parent = null;
        }
    }

    /**
     * Returns the element represented by this info
     *
     * @return the element in play
     */
    public GraphQLSchemaElement getElement() {
        return element;
    }

    /**
     * @return an element MAY have an optional parent
     */
    public Optional<GraphqlElementParentTree> getParentInfo() {
        return Optional.ofNullable(parent);
    }

    /**
     * @return the tree as a list of types
     */
    public List<GraphQLSchemaElement> toList() {
        List<GraphQLSchemaElement> types = new ArrayList<>();
        types.add(element);
        Optional<GraphqlElementParentTree> parentInfo = this.getParentInfo();
        while (parentInfo.isPresent()) {
            types.add(parentInfo.get().getElement());
            parentInfo = parentInfo.get().getParentInfo();
        }
        return types;
    }

    @Override
    public String toString() {
        return String.valueOf(element) +
                " - parent : " +
                parent;
    }
}
