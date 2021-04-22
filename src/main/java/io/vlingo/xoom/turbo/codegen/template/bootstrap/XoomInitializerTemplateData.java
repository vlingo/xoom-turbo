// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template.bootstrap;

import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.content.Content;
import io.vlingo.xoom.turbo.codegen.content.ContentQuery;
import io.vlingo.xoom.turbo.codegen.parameter.Label;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameter;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;
import io.vlingo.xoom.turbo.codegen.template.storage.StorageType;

import java.util.List;
import java.util.Optional;

import static io.vlingo.xoom.turbo.codegen.parameter.Label.*;
import static io.vlingo.xoom.turbo.codegen.template.TemplateStandard.*;

public class XoomInitializerTemplateData extends BootstrapTemplateData {

  @Override
  protected void enrichParameters(final CodeGenerationContext context) {
    final List<Content> contents = context.contents();
    final String appName = context.parameterOf(Label.APPLICATION_NAME);
    final String xoomInitializerClass = context.parameterOf(XOOM_INITIALIZER_NAME);
    final Boolean blockingMessaging = context.parameterOf(BLOCKING_MESSAGING, Boolean::valueOf);
    final Boolean customInitialization = !xoomInitializerClass.equals(XOOM_INITIALIZER.resolveClassname());

    loadImports(context, contents);

    parameters().and(TemplateParameter.BLOCKING_MESSAGING, blockingMessaging)
            .and(TemplateParameter.XOOM_INITIALIZER_CLASS, xoomInitializerClass)
            .and(TemplateParameter.CUSTOM_INITIALIZATION, customInitialization)
            .and(TemplateParameter.REST_RESOURCES, RestResource.from(contents))
            .and(TemplateParameter.APPLICATION_NAME, appName)
            .and(TemplateParameter.EXCHANGE_BOOTSTRAP_NAME, resolveExchangeBootstrapName(context));
  }

  private void loadImports(final CodeGenerationContext context, final List<Content> contents) {
    final Boolean useCQRS =
            context.parameterOf(CQRS, Boolean::valueOf);

    final StorageType storageType =
            context.parameterOf(STORAGE_TYPE, StorageType::valueOf);

    final TemplateStandard[] dependencies =
            new TemplateStandard[]{STORE_PROVIDER, PROJECTION_DISPATCHER_PROVIDER,
                    REST_RESOURCE, AUTO_DISPATCH_RESOURCE_HANDLER, EXCHANGE_BOOTSTRAP};

    parameters().addImports(ContentQuery.findFullyQualifiedClassNames(contents, dependencies))
            .addImports(storageType.resolveTypeRegistryQualifiedNames(useCQRS));
  }

  private String resolveExchangeBootstrapName(final CodeGenerationContext context) {
    final Optional<String> exchangeBootstrapQualifiedName =
            ContentQuery.findFullyQualifiedClassNames(EXCHANGE_BOOTSTRAP, context.contents()).stream().findFirst();

    if (!exchangeBootstrapQualifiedName.isPresent()) {
      return null;
    }

    return exchangeBootstrapQualifiedName.get();
  }

  @Override
  protected String resolvePackage(final String basePackage) {
    return basePackage;
  }

  @Override
  protected boolean support(final CodeGenerationContext context) {
    return context.isInternalGeneration();
  }

  @Override
  public TemplateStandard standard() {
    return TemplateStandard.XOOM_INITIALIZER;
  }

}