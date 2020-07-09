// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer.methods;

import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.http.resource.Resources;
import io.vlingo.xoom.annotation.ClassRetriever;
import io.vlingo.xoom.annotation.initializer.ResourceHandlers;
import org.apache.commons.lang3.ArrayUtils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.util.Elements;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.annotation.initializer.XoomInitializerStatements.RESOURCES_STATEMENT_PATTERN;
import static io.vlingo.xoom.annotation.initializer.XoomInitializerStatements.ROUTES_STATEMENT;

public class ResourcesStatementResolver {

    private final Elements elements;
    private final TypeElement bootstrapClass;

    public static Entry<String, Object[]> resolve(final Elements elements,
                                                  final TypeElement bootstrapClass) {
        return new ResourcesStatementResolver(elements, bootstrapClass).resolve();
    }

    private ResourcesStatementResolver(final Elements elements, final TypeElement bootstrapClass) {
        this.elements = elements;
        this.bootstrapClass = bootstrapClass;
    }

    private Entry<String, Object[]> resolve() {
        final Object[] defaultParameters = new Object[]{Resources.class, Resources.class};

        final ResourceHandlers resourceHandlersAnnotation =
                bootstrapClass.getAnnotation(ResourceHandlers.class);

        if(shouldIgnore(resourceHandlersAnnotation)){
            final String defaultStatement =
                    String.format(RESOURCES_STATEMENT_PATTERN, "");

            return new SimpleEntry(defaultStatement, defaultParameters);
        }

        final Object[] resourcesClasses =
                retrieveResourcesClasses(resourceHandlersAnnotation);

        final String routesStatement =
                Stream.of(resourcesClasses).map(clazz -> ROUTES_STATEMENT)
                        .collect(Collectors.joining(", "));

        return new SimpleEntry(String.format(RESOURCES_STATEMENT_PATTERN, routesStatement),
                ArrayUtils.addAll(defaultParameters, resourcesClasses));
    }

    private Object[] retrieveResourcesClasses(final ResourceHandlers annotation) {
        try {
            if(isPackageBased(annotation)) {
                return ClassRetriever.with(elements)
                        .subclassesOf(ResourceHandler.class, annotation.packages())
                        .toArray();
            }
            return annotation.value();
        } catch (final MirroredTypesException exception) {
            return exception.getTypeMirrors().toArray();
        }
    }

    private boolean isPackageBased(final ResourceHandlers resourceHandlersAnnotation) {
        final String[] packages = resourceHandlersAnnotation.packages();
        if(packages.length == 1 && packages[0].isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean shouldIgnore(final ResourceHandlers resourceHandlersAnnotation) {
        try {
            return resourceHandlersAnnotation == null ||
                    (resourceHandlersAnnotation.value().length == 0 &&
                            !isPackageBased(resourceHandlersAnnotation));
        } catch (final MirroredTypesException exception) {
            return false;
        }
    }

}