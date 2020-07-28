// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.initializer.contentloader;

import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.xoom.annotation.ClassRetriever;
import io.vlingo.xoom.annotation.initializer.ResourceHandlers;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RestResourceContentLoader extends TypeBasedContentLoader {

    protected RestResourceContentLoader(final Element annotatedClass,
                                        final ProcessingEnvironment environment) {
        super(annotatedClass, environment);
    }

    @Override
    protected List<TypeElement> retrieveTypes() {
        final ResourceHandlers resourceHandlers =
                annotatedClass.getAnnotation(ResourceHandlers.class);

        if(shouldIgnore(resourceHandlers)) {
            return Collections.emptyList();
        }

        if(isPackageBased(resourceHandlers)) {
            return ClassRetriever.with(environment.getElementUtils())
                    .subclassesOf(ResourceHandler.class, resourceHandlers.packages())
                    .map(this::toType).collect(Collectors.toList());
        }

        return retrieveTypes(resourceHandlers, annotation -> resourceHandlers.value());
    }

    @Override
    protected TemplateStandard standard() {
        return TemplateStandard.REST_RESOURCE;
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
