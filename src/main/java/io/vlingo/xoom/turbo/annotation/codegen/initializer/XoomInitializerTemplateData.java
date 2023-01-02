// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.codegen.initializer;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.turbo.annotation.codegen.AnnotationBasedTemplateStandard;
import io.vlingo.xoom.turbo.annotation.codegen.Label;
import io.vlingo.xoom.turbo.annotation.codegen.TemplateParameter;
import io.vlingo.xoom.turbo.annotation.codegen.projections.ProjectionType;
import io.vlingo.xoom.turbo.annotation.codegen.storage.StorageType;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.ParameterKey.Defaults.PACKAGE_NAME;
import static io.vlingo.xoom.turbo.annotation.codegen.Label.BLOCKING_MESSAGING;
import static io.vlingo.xoom.turbo.annotation.codegen.Label.PROJECTION_TYPE;
import static io.vlingo.xoom.turbo.annotation.codegen.Label.STORAGE_TYPE;
import static io.vlingo.xoom.turbo.annotation.codegen.Label.*;
import static io.vlingo.xoom.turbo.annotation.codegen.TemplateParameter.APPLICATION_NAME;
import static io.vlingo.xoom.turbo.annotation.codegen.TemplateParameter.USE_ANNOTATIONS;
import static io.vlingo.xoom.turbo.annotation.codegen.TemplateParameter.*;

public class XoomInitializerTemplateData extends TemplateData {

  private final TemplateParameters parameters;

  public XoomInitializerTemplateData(final CodeGenerationContext context) {
    final List<Content> contents = context.contents();
    final String packageName = context.parameterOf(PACKAGE);
    final Boolean useCQRS = context.parameterOf(CQRS, Boolean::valueOf);
    final String xoomInitializerClass = context.parameterOf(XOOM_INITIALIZER_NAME);
    final Boolean hasExchange = ContentQuery.exists(AnnotationBasedTemplateStandard.EXCHANGE_BOOTSTRAP, context.contents());
    final StorageType storageType = context.parameterOf(STORAGE_TYPE, StorageType::valueOf);
    final ProjectionType projectionType = context.parameterOf(PROJECTION_TYPE, ProjectionType::valueOf);
    final Boolean blockingMessaging = context.parameterOf(BLOCKING_MESSAGING, Boolean::valueOf);
    final Boolean customInitialization = !xoomInitializerClass.equals(AnnotationBasedTemplateStandard.XOOM_INITIALIZER.resolveClassname());

    this.parameters = TemplateParameters.with(TemplateParameter.BLOCKING_MESSAGING, blockingMessaging)
            .and(APPLICATION_NAME, context.parameterOf(Label.APPLICATION_NAME)).and(PACKAGE_NAME, packageName)
            .and(PROVIDERS, StoreProvider.from(storageType, useCQRS, projectionType.isProjectionEnabled(), hasExchange))
            .and(USE_ANNOTATIONS, context.parameterOf(Label.USE_ANNOTATIONS, Boolean::valueOf))
            .andResolve(PROJECTION_DISPATCHER_PROVIDER_NAME, AnnotationBasedTemplateStandard.PROJECTION_DISPATCHER_PROVIDER::resolveClassname)
            .and(XOOM_INITIALIZER_CLASS, context.parameterOf(XOOM_INITIALIZER_NAME))
            .and(EXCHANGE_BOOTSTRAP_NAME, resolveExchangeBootstrapName(context))
            .and(TYPE_REGISTRIES, TypeRegistry.from(storageType, useCQRS))
            .and(USE_PROJECTIONS, projectionType.isProjectionEnabled())
            .and(CUSTOM_INITIALIZATION, customInitialization)
            .and(REST_RESOURCES, RestResource.from(contents))
            .addImports(resolveImports(context));
  }

  private Set<String> resolveImports(final CodeGenerationContext context) {
    final Boolean useCQRS =
            context.parameterOf(CQRS, Boolean::valueOf);

    final StorageType storageType =
            context.parameterOf(STORAGE_TYPE, StorageType::valueOf);

    final TemplateStandard[] dependencies =
            new TemplateStandard[]{AnnotationBasedTemplateStandard.STORE_PROVIDER, AnnotationBasedTemplateStandard.PROJECTION_DISPATCHER_PROVIDER,
                    AnnotationBasedTemplateStandard.REST_RESOURCE, AnnotationBasedTemplateStandard.AUTO_DISPATCH_RESOURCE_HANDLER, AnnotationBasedTemplateStandard.EXCHANGE_BOOTSTRAP};

    return Stream.of(ContentQuery.findFullyQualifiedClassNames(context.contents(), dependencies),
            storageType.resolveTypeRegistryQualifiedNames(useCQRS)).flatMap(Set::stream)
            .collect(Collectors.toSet());
  }

  private String resolveExchangeBootstrapName(final CodeGenerationContext context) {
    final Optional<String> exchangeBootstrapQualifiedName =
            ContentQuery.findFullyQualifiedClassNames(AnnotationBasedTemplateStandard.EXCHANGE_BOOTSTRAP, context.contents()).stream().findFirst();

    if (!exchangeBootstrapQualifiedName.isPresent()) {
      return null;
    }

    return exchangeBootstrapQualifiedName.get();
  }

  @Override
  public TemplateParameters parameters() {
    return parameters;
  }

  @Override
  public TemplateStandard standard() {
    return AnnotationBasedTemplateStandard.XOOM_INITIALIZER;
  }

}