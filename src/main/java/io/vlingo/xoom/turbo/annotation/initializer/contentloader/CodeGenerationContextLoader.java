// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.initializer.contentloader;

import io.vlingo.xoom.turbo.annotation.AnnotatedElements;
import io.vlingo.xoom.turbo.annotation.autodispatch.AutoDispatch;
import io.vlingo.xoom.turbo.annotation.autodispatch.AutoDispatchParameterResolver;
import io.vlingo.xoom.turbo.annotation.initializer.Xoom;
import io.vlingo.xoom.turbo.annotation.initializer.XoomInitializerParameterResolver;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence;
import io.vlingo.xoom.turbo.annotation.persistence.PersistenceParameterResolver;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.CodeGenerationLocation;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.*;

import static io.vlingo.xoom.turbo.annotation.codegen.template.Label.GENERATION_LOCATION;

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
            .with(GENERATION_LOCATION, CodeGenerationLocation.INTERNAL.name())
            .contents(resolveContentLoaders());
  }

  @SuppressWarnings("rawtypes")
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