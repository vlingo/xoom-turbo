// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer.contentloader;

import io.vlingo.xoom.turbo.annotation.persistence.Adapters;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;

import static io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard.AGGREGATE_STATE;
import static io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard.DOMAIN_EVENT;


public class AdapterEntriesContentLoader extends TypeBasedContentLoader {

  protected AdapterEntriesContentLoader(final Element annotatedClass,
                                        final ProcessingEnvironment environment) {
    super(annotatedClass, environment);
  }

  @Override
  protected List<TypeElement> retrieveContentSource() {
    final Adapters adapters = annotatedClass.getAnnotation(Adapters.class);

    if (adapters == null) {
      return Collections.emptyList();
    }

    return typeRetriever.typesFrom(adapters, Adapters::value);
  }

  @Override
  protected TemplateStandard standard() {
    final Persistence persistence = annotatedClass.getAnnotation(Persistence.class);
    if (persistence.storageType().isJournal()) {
      return DOMAIN_EVENT;
    }
    return AGGREGATE_STATE;
  }

}
