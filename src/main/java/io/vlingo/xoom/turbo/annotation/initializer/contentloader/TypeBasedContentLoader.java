// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer.contentloader;

import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.List;

public abstract class TypeBasedContentLoader extends ContentLoader<List<TypeElement>> {

  protected TypeBasedContentLoader(final Element annotatedClass,
                                   final ProcessingEnvironment environment) {
    super(annotatedClass, environment);
  }

  public void load(final CodeGenerationContext context) {
    this.retrieveContentSource()
            .forEach(typeElement -> context.addContent(standard(), typeElement));
  }

  protected abstract TemplateStandard standard();

}