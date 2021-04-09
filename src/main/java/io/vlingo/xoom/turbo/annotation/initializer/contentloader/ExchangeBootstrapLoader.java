// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.initializer.contentloader;

import io.vlingo.xoom.turbo.annotation.Context;
import io.vlingo.xoom.turbo.annotation.PackageCollector;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;
import io.vlingo.xoom.turbo.exchange.ExchangeInitializer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ExchangeBootstrapLoader extends TypeBasedContentLoader {

  protected ExchangeBootstrapLoader(final Element annotatedClass,
                                    final ProcessingEnvironment environment) {
    super(annotatedClass, environment);
  }

  @Override
  protected List<TypeElement> retrieveContentSource() {
    final Persistence persistence = annotatedClass.getAnnotation(Persistence.class);

    final Path baseDirectory =
            Context.locateBaseDirectory(environment.getFiler());

    final String[] allPackages =
            PackageCollector.from(baseDirectory, persistence.basePackage())
                    .collectAll().toArray(new String[]{});

    return typeRetriever.subclassesOf(ExchangeInitializer.class, allPackages)
            .map(this::toType).collect(Collectors.toList());
  }

  private TypeElement toType(final TypeMirror typeMirror) {
    return (TypeElement) environment.getTypeUtils().asElement(typeMirror);
  }

  @Override
  protected TemplateStandard standard() {
    return TemplateStandard.EXCHANGE_BOOTSTRAP;
  }
}
