// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer.contentloader;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.ContentLoader;
import io.vlingo.xoom.turbo.annotation.AnnotatedElements;
import io.vlingo.xoom.turbo.annotation.autodispatch.AutoDispatch;
import io.vlingo.xoom.turbo.annotation.autodispatch.AutoDispatchParameterResolver;
import io.vlingo.xoom.turbo.annotation.initializer.Xoom;
import io.vlingo.xoom.turbo.annotation.initializer.XoomInitializerParameterResolver;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence;
import io.vlingo.xoom.turbo.annotation.persistence.PersistenceParameterResolver;

public class CodeGenerationContextLoader {

  private final Filer filer;
  private final String basePackage;
  private final ProcessingEnvironment environment;
  private final TypeElement bootstrapClass;
  private final TypeElement persistenceSetupClass;
  private final Set<TypeElement> autoDispatchResourceClasses = new HashSet<>();

  public static CodeGenerationContext from(final Filer filer,
                                           final String basePackage,
                                           final AnnotatedElements elements,
                                           final ProcessingEnvironment environment) {
    return new CodeGenerationContextLoader(filer, basePackage,
            elements, environment).load();
  }

  public CodeGenerationContextLoader(final Filer filer,
                                     final String basePackage,
                                     final AnnotatedElements elements,
                                     final ProcessingEnvironment environment) {
    this.filer = filer;
    this.environment = environment;
    this.basePackage = basePackage;
    this.bootstrapClass = elements.elementWith(Xoom.class);
    this.persistenceSetupClass = elements.elementWith(Persistence.class);
    this.autoDispatchResourceClasses.addAll(elements.elementsWith(AutoDispatch.class));
  }

  public CodeGenerationContext load() {
    return CodeGenerationContext.using(filer, bootstrapClass)
            .on(XoomInitializerParameterResolver.from(basePackage, bootstrapClass, environment).resolve())
            .on(AutoDispatchParameterResolver.from(autoDispatchResourceClasses, environment).resolve())
            .on(PersistenceParameterResolver.from(persistenceSetupClass, environment).resolve())
            .contents(resolveContentLoaders());
  }

  private List<ContentLoader> resolveContentLoaders() {
    if (bootstrapClass == null) {
      return Collections.emptyList();
    }
    return Arrays.asList(new ProjectionActorContentLoader(persistenceSetupClass, environment),
            new AdapterEntriesContentLoader(persistenceSetupClass, environment),
            new DataObjectContentLoader(persistenceSetupClass, environment),
            new QueriesContentLoader(persistenceSetupClass, environment),
            new AggregateContentLoader(persistenceSetupClass, environment),
            new RestResourceContentLoader(bootstrapClass, environment),
            new ExchangeBootstrapLoader(persistenceSetupClass, environment));
  }

}