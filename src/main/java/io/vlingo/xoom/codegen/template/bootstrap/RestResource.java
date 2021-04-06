// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.autodispatch.AutoDispatchResourceHandlerDetail;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.MODEL;
import static io.vlingo.xoom.codegen.template.TemplateParameter.STORAGE_TYPE;
import static io.vlingo.xoom.codegen.template.TemplateParameters.with;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AUTO_DISPATCH_RESOURCE_HANDLER;
import static io.vlingo.xoom.codegen.template.TemplateStandard.REST_RESOURCE;
import static io.vlingo.xoom.codegen.template.storage.Model.QUERY;
import static io.vlingo.xoom.codegen.template.storage.StorageType.STATE_STORE;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class RestResource {

    private final String className;
    private final String objectName;
    private final String arguments;
    private final boolean last;

    public static List<RestResource> from(final CodeGenerationContext context) {
        final Set<String> qualifiedNames =
                ContentQuery.findFullyQualifiedClassNames(context.contents(),
                        REST_RESOURCE, AUTO_DISPATCH_RESOURCE_HANDLER);

        final Iterator<String> iterator = qualifiedNames.iterator();

        return IntStream
                .range(0, qualifiedNames.size())
                .mapToObj(index -> new RestResource(iterator.next(), context, index, qualifiedNames.size()))
                .collect(toList());
    }

    private RestResource(final String restResourceQualifiedName,
                         final CodeGenerationContext context,
                         final int resourceIndex,
                         final int numberOfResources) {
        this.className = CodeElementFormatter.simpleNameOf(restResourceQualifiedName);
        this.objectName = CodeElementFormatter.qualifiedNameToAttribute(restResourceQualifiedName);
        this.arguments = resolveArguments(restResourceQualifiedName, context);
        this.last = resourceIndex == numberOfResources - 1;
    }

    private String resolveArguments(final String restResourceName, final CodeGenerationContext context) {
        final Stream<String> defaultArgument = Stream.of("grid");

        final Optional<String> queriesActorInstancePath =
                resolveQueriesActorInstancePath(restResourceName, context);

        final Stream<String> queriesArgument =
                queriesActorInstancePath.isPresent() ?
                        Stream.of(queriesActorInstancePath.get()) : Stream.empty();

        return Stream.of(defaultArgument, queriesArgument).flatMap(s -> s).collect(joining(", "));
    }

    private Optional<String> resolveQueriesActorInstancePath(final String restResourceQualifiedName, final CodeGenerationContext context) {
        final Optional<String> queriesAttributeName =
                AutoDispatchResourceHandlerDetail.findQueriesAttributeName(restResourceQualifiedName, context);

        if(!queriesAttributeName.isPresent()) {
            return Optional.empty();
        }

        final String storeProviderClass =
                TemplateStandard.STORE_PROVIDER.resolveClassname(with(STORAGE_TYPE, STATE_STORE).and(MODEL, QUERY));

        final String path =
                String.format("%s.%s", CodeElementFormatter.simpleNameToAttribute(storeProviderClass), queriesAttributeName.get());

        return Optional.of(path);
    }

    public String getClassName() {
        return className;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getArguments() {
        return arguments;
    }

    public boolean isLast() {
        return last;
    }

}
