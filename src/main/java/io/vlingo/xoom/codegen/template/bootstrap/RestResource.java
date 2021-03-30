// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.xoom.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.storage.Model;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.MODEL;
import static io.vlingo.xoom.codegen.template.TemplateParameter.STORAGE_TYPE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AUTO_DISPATCH_RESOURCE_HANDLER;
import static io.vlingo.xoom.codegen.template.TemplateStandard.REST_RESOURCE;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class RestResource {

    private final String className;
    private final String objectName;
    private final String arguments;
    private final boolean last;

    public static List<RestResource> from(boolean useCQRS, final List<Content> contents) {
        final Set<String> classNames =
                ContentQuery.findClassNames(contents, REST_RESOURCE,
                        AUTO_DISPATCH_RESOURCE_HANDLER);

        final Iterator<String> iterator = classNames.iterator();

        return IntStream
                .range(0, classNames.size())
                .mapToObj(index -> new RestResource(iterator.next(), useCQRS, index, classNames.size()))
                .collect(toList());
    }

    private RestResource(final String restResourceName,
                         final Boolean useCQRS,
                         final int resourceIndex,
                         final int numberOfResources) {
        this.className = restResourceName;
        this.objectName = CodeElementFormatter.simpleNameToAttribute(restResourceName);
        this.arguments = resolveArguments(restResourceName, useCQRS);
        this.last = resourceIndex == numberOfResources - 1;
    }

    private String resolveArguments(String restResourceName, Boolean useCQRS) {
        final List<String> arguments = Stream.of("grid").collect(toList());

        if (useCQRS) {
            final String storeProviderClass = TemplateStandard.STORE_PROVIDER.resolveClassname(TemplateParameters.with(STORAGE_TYPE, StorageType.STATE_STORE).and(MODEL, Model.QUERY));
            final String storeProviderName = CodeElementFormatter.simpleNameToAttribute(storeProviderClass);
            final String queriesClassName = TemplateStandard.QUERIES.resolveClassname(restResourceName.replace("Resource", ""));
            final String queriesAttributeName = CodeElementFormatter.simpleNameToAttribute(queriesClassName);
            arguments.add(String.format("%s.%s", storeProviderName, queriesAttributeName));
        }

        return arguments.stream().collect(joining(", "));

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
