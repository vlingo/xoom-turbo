// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer.contentloader;

import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.turbo.annotation.codegen.AnnotationBasedTemplateStandard;
import io.vlingo.xoom.turbo.annotation.initializer.ResourceHandlers;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RestResourceContentLoader extends TypeBasedContentLoader {

  protected RestResourceContentLoader(final Element annotatedClass,
                                      final ProcessingEnvironment environment) {
    super(annotatedClass, environment);
  }

  @Override
  protected List<TypeElement> retrieveContentSource() {

    final ResourceHandlers resourceHandlers =
            annotatedClass.getAnnotation(ResourceHandlers.class);

    if (shouldIgnore(resourceHandlers)) {
      return Collections.emptyList();
    }

    if (isPackageBased(resourceHandlers)) {
      return typeRetriever.subclassesOf(DynamicResourceHandler.class, resourceHandlers.packages())
              .map(this::toType).collect(Collectors.toList());
    }

    return typeRetriever.typesFrom(resourceHandlers, ResourceHandlers::value);
  }

  @Override
  protected TemplateStandard standard() {
    return AnnotationBasedTemplateStandard.REST_RESOURCE;
  }

  private TypeElement toType(final TypeMirror typeMirror) {
    return (TypeElement) environment.getTypeUtils().asElement(typeMirror);
  }

  private boolean isPackageBased(final ResourceHandlers resourceHandlersAnnotation) {
    final String[] packages = resourceHandlersAnnotation.packages();
    return packages.length != 1 || !packages[0].isEmpty();
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
