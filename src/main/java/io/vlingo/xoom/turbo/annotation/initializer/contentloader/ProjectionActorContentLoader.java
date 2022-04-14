// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer.contentloader;

import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.turbo.annotation.persistence.Projection;
import io.vlingo.xoom.turbo.annotation.persistence.Projections;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.annotation.codegen.AnnotationBasedTemplateStandard.PROJECTION;


public class ProjectionActorContentLoader extends TypeBasedContentLoader {

  protected ProjectionActorContentLoader(final Element annotatedClass,
                                         final ProcessingEnvironment environment) {
    super(annotatedClass, environment);
  }

  @Override
  protected List<TypeElement> retrieveContentSource() {
    final Projections projections = annotatedClass.getAnnotation(Projections.class);

    if (projections == null) {
      return Collections.emptyList();
    }

    return Stream.of(projections.value())
            .map(projection -> typeRetriever.from(projection, Projection::actor))
            .collect(Collectors.toList());
  }

  @Override
  protected TemplateStandard standard() {
    return PROJECTION;
  }

}
