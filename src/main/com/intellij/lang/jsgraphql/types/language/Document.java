package com.intellij.lang.jsgraphql.types.language;


import com.google.common.collect.ImmutableList;
import com.intellij.lang.jsgraphql.types.Internal;
import com.intellij.lang.jsgraphql.types.PublicApi;
import com.intellij.lang.jsgraphql.types.collect.ImmutableKit;
import com.intellij.lang.jsgraphql.types.util.TraversalControl;
import com.intellij.lang.jsgraphql.types.util.TraverserContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.intellij.lang.jsgraphql.types.Assert.assertNotNull;
import static com.intellij.lang.jsgraphql.types.collect.ImmutableKit.emptyList;
import static com.intellij.lang.jsgraphql.types.collect.ImmutableKit.emptyMap;
import static com.intellij.lang.jsgraphql.types.language.NodeChildrenContainer.newNodeChildrenContainer;

@PublicApi
public class Document extends AbstractNode<Document> {

    private final ImmutableList<Definition> definitions;

    public static final String CHILD_DEFINITIONS = "definitions";

    @Internal
    protected Document(List<Definition> definitions, SourceLocation sourceLocation, List<Comment> comments, IgnoredChars ignoredChars, Map<String, String> additionalData) {
        super(sourceLocation, comments, ignoredChars, additionalData);
        this.definitions = ImmutableList.copyOf(definitions);
    }

    /**
     * alternative to using a Builder for convenience
     *
     * @param definitions the definitions that make up this document
     */
    public Document(List<Definition> definitions) {
        this(definitions, null, emptyList(), IgnoredChars.EMPTY, emptyMap());
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    /**
     * Returns a list of definitions of the specific type.  It uses {@link Class#isAssignableFrom(Class)} for the test
     *
     * @param definitionClass the definition class
     * @param <T>             the type of definition
     *
     * @return a list of definitions of that class or empty list
     */
    public <T extends Definition> List<T> getDefinitionsOfType(Class<T> definitionClass) {
        return definitions.stream()
                .filter(d -> definitionClass.isAssignableFrom(d.getClass()))
                .map(definitionClass::cast)
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public List<Node> getChildren() {
        return ImmutableList.copyOf(definitions);
    }

    @Override
    public NodeChildrenContainer getNamedChildren() {
        return newNodeChildrenContainer()
                .children(CHILD_DEFINITIONS, definitions)
                .build();
    }

    @Override
    public Document withNewChildren(NodeChildrenContainer newChildren) {
        return transform(builder -> builder
                .definitions(newChildren.getChildren(CHILD_DEFINITIONS))
        );
    }

    @Override
    public boolean isEqualTo(Node o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return true;
    }

    @Override
    public Document deepCopy() {
        return new Document(deepCopy(definitions), getSourceLocation(), getComments(), getIgnoredChars(), getAdditionalData());
    }

    @Override
    public String toString() {
        return "Document{" +
                "definitions=" + definitions +
                '}';
    }

    @Override
    public TraversalControl accept(TraverserContext<Node> context, NodeVisitor visitor) {
        return visitor.visitDocument(this, context);
    }

    public static Builder newDocument() {
        return new Builder();
    }

    public Document transform(Consumer<Builder> builderConsumer) {
        Builder builder = new Builder(this);
        builderConsumer.accept(builder);
        return builder.build();
    }

    public static final class Builder implements NodeBuilder {
        private ImmutableList<Definition> definitions = emptyList();
        private SourceLocation sourceLocation;
        private ImmutableList<Comment> comments = emptyList();
        private IgnoredChars ignoredChars = IgnoredChars.EMPTY;
        private Map<String, String> additionalData = new LinkedHashMap<>();

        private Builder() {
        }

        private Builder(Document existing) {
            this.sourceLocation = existing.getSourceLocation();
            this.comments = ImmutableList.copyOf(existing.getComments());
            this.definitions = ImmutableList.copyOf(existing.getDefinitions());
            this.ignoredChars = existing.getIgnoredChars();
            this.additionalData = new LinkedHashMap<>(existing.getAdditionalData());
        }

        public Builder definitions(List<Definition> definitions) {
            this.definitions = ImmutableList.copyOf(definitions);
            return this;
        }

        public Builder definition(Definition definition) {
            this.definitions = ImmutableKit.addToList(definitions,definition);
            return this;
        }

        public Builder sourceLocation(SourceLocation sourceLocation) {
            this.sourceLocation = sourceLocation;
            return this;
        }

        public Builder comments(List<Comment> comments) {
            this.comments = ImmutableList.copyOf(comments);
            return this;
        }

        public Builder ignoredChars(IgnoredChars ignoredChars) {
            this.ignoredChars = ignoredChars;
            return this;
        }

        public Builder additionalData(Map<String, String> additionalData) {
            this.additionalData = assertNotNull(additionalData);
            return this;
        }

        public Builder additionalData(String key, String value) {
            this.additionalData.put(key, value);
            return this;
        }


        public Document build() {
            return new Document(definitions, sourceLocation, comments, ignoredChars, additionalData);
        }
    }
}
