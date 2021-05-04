// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.initializer.contentloader;

import io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard;
import io.vlingo.xoom.turbo.annotation.persistence.DataObjects;
import io.vlingo.xoom.turbo.codegen.template.DesignerTemplateStandard;
import io.vlingo.xoom.turbo.codegen.template.DesignerTemplateStandard;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;

public class DataObjectContentLoader extends TypeBasedContentLoader {

  protected DataObjectContentLoader(final Element annotatedClass,
                                    final ProcessingEnvironment environment) {
    super(annotatedClass, environment);
  }

  @Override
  protected List<TypeElement> retrieveContentSource() {
    final DataObjects dataObjects = annotatedClass.getAnnotation(DataObjects.class);

    if (dataObjects == null) {
      return Collections.emptyList();
    }

    return typeRetriever.typesFrom(dataObjects, DataObjects::value);
  }

  @Override
  protected TemplateStandard standard() {
    return AnnotationBasedTemplateStandard.DATA_OBJECT;
  }
}
