package com.intellij.lang.jsgraphql.types.language;

import com.intellij.lang.jsgraphql.types.PublicApi;

/**
 * Represents a node that can contain a description.
 */
@PublicApi
public interface DescribedNode<T extends Node> extends Node<T> {

    /**
     * @return the description of this node
     */
    Description getDescription();

}
