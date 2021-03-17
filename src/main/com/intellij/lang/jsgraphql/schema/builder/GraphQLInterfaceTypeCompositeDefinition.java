package com.intellij.lang.jsgraphql.schema.builder;

import com.intellij.util.containers.ContainerUtil;
import com.intellij.lang.jsgraphql.types.language.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.intellij.lang.jsgraphql.schema.GraphQLTypeDefinitionUtil.*;

public class GraphQLInterfaceTypeCompositeDefinition
    extends GraphQLExtendableCompositeDefinition<InterfaceTypeDefinition, InterfaceTypeExtensionDefinition> {

    private final Set<String> myImplements = new HashSet<>();
    private final Set<String> myDirectives = new HashSet<>();
    private final Set<String> myFieldDefinitions = new HashSet<>();

    @NotNull
    @Override
    protected InterfaceTypeDefinition mergeDefinitions() {
        Map<String, Directive> directives = new LinkedHashMap<>();
        Map<String, FieldDefinition> fieldDefinitions = new LinkedHashMap<>();

        for (InterfaceTypeDefinition definition : myDefinitions) {
            mergeNodes(directives, mapNamedNodesByKey(definition.getDirectives()));
            mergeNodes(fieldDefinitions, mapNamedNodesByKey(definition.getFieldDefinitions()));
        }

        InterfaceTypeDefinition definition = ContainerUtil.getFirstItem(myDefinitions);

        myDirectives.addAll(directives.keySet());
        myFieldDefinitions.addAll(fieldDefinitions.keySet());
        myImplements.addAll(mapTypeNodesByKey(definition.getImplements()).keySet());

        return definition.transform(builder ->
            builder
                .directives(toList(directives))
                .definitions(toList(fieldDefinitions))
                .implementz(definition.getImplements()) // https://github.com/graphql-java/graphql-java/issues/1974
        );
    }

    @Override
    protected @NotNull List<InterfaceTypeExtensionDefinition> processExtensions() {
        return ContainerUtil.map(myExtensions, extension -> {
            Map<String, Directive> directives = mergeExtensionNodes(mapNamedNodesByKey(extension.getDirectives()), myDirectives);
            Map<String, FieldDefinition> definitions =
                mergeExtensionNodes(mapNamedNodesByKey(extension.getFieldDefinitions()), myFieldDefinitions);
            @SuppressWarnings({"unchecked", "rawtypes"})
            Map<String, Type> implementz = mergeExtensionNodes(mapTypeNodesByKey(extension.getImplements()), myImplements);

            return extension.transformExtension(builder ->
                builder.directives(toList(directives)).definitions(toList(definitions)).implementz(toList(implementz))
            );
        });
    }
}
