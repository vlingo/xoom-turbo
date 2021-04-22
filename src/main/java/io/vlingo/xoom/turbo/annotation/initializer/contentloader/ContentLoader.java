// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.initializer.contentloader;

import io.vlingo.xoom.turbo.annotation.TypeRetriever;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public abstract class ContentLoader<T> {

  protected final Element annotatedClass;
  protected final TypeRetriever typeRetriever;
  protected final ProcessingEnvironment environment;

  protected ContentLoader(final Element annotatedClass,
                          final ProcessingEnvironment environment) {
    this.environment = environment;
    this.annotatedClass = annotatedClass;
    this.typeRetriever = TypeRetriever.with(environment);
  }

  public abstract void load(final CodeGenerationContext context);

  public boolean shouldLoad() {
    return annotatedClass != null;
  }

  protected abstract T retrieveContentSource();

}
