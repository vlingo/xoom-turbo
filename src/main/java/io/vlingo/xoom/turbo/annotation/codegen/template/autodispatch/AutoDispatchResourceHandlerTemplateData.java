// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.template.autodispatch;

import io.vlingo.xoom.turbo.annotation.codegen.template.Label;
import io.vlingo.xoom.turbo.annotation.codegen.template.storage.Model;
import io.vlingo.xoom.turbo.annotation.codegen.template.storage.Queries;
import io.vlingo.xoom.turbo.annotation.codegen.template.storage.StorageType;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.turbo.codegen.content.ContentQuery;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateParameters;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard.*;
import static io.vlingo.xoom.turbo.annotation.codegen.template.Label.*;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.HANDLERS_CONFIG_NAME;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.MODEL_ACTOR;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.MODEL_PROTOCOL;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.QUERIES;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.STORAGE_TYPE;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.URI_ROOT;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.USE_AUTO_DISPATCH;
import static io.vlingo.xoom.turbo.codegen.template.TemplateParameter.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class AutoDispatchResourceHandlerTemplateData extends TemplateData {

  private final String restResourceName;
  private final TemplateParameters parameters;

  public static List<TemplateData> from(final CodeGenerationContext context) {
    return context.parametersOf(AUTO_DISPATCH_NAME)
            .map(param -> new AutoDispatchResourceHandlerTemplateData(context, param))
            .collect(toList());
  }

  @SuppressWarnings("unchecked")
  private AutoDispatchResourceHandlerTemplateData(final CodeGenerationContext context,
                                                  final CodeGenerationParameter autoDispatchParameter) {
    this.restResourceName = CodeElementFormatter.simpleNameOf(autoDispatchParameter.value);

    final TemplateParameters queryStoreProviderParameters =
            TemplateParameters.with(STORAGE_TYPE, StorageType.STATE_STORE).and(MODEL, Model.QUERY);

    final String queryStoreProviderName =
            STORE_PROVIDER.resolveClassname(queryStoreProviderParameters);

    final String aggregateProtocolClassName =
            CodeElementFormatter.simpleNameOf(autoDispatchParameter.retrieveRelatedValue(Label.MODEL_PROTOCOL));

    this.parameters =
            TemplateParameters.with(PACKAGE_NAME, CodeElementFormatter.packageOf(autoDispatchParameter.value))
                    .and(QUERIES, Queries.from(autoDispatchParameter))
                    .and(STATE_NAME, AGGREGATE_STATE.resolveClassname(aggregateProtocolClassName))
                    .and(REST_RESOURCE_NAME, standard().resolveClassname(restResourceName))
                    .and(URI_ROOT, autoDispatchParameter.retrieveRelatedValue(Label.URI_ROOT))
                    .and(ROUTE_DECLARATIONS, RouteDeclaration.from(autoDispatchParameter))
                    .and(MODEL_PROTOCOL, autoDispatchParameter.retrieveRelatedValue(Label.MODEL_PROTOCOL))
                    .and(MODEL_ACTOR, autoDispatchParameter.retrieveRelatedValue(Label.MODEL_ACTOR))
                    .and(HANDLERS_CONFIG_NAME, autoDispatchParameter.retrieveRelatedValue(Label.HANDLERS_CONFIG_NAME))
                    .and(STORE_PROVIDER_NAME, queryStoreProviderName).and(ROUTE_METHODS, new ArrayList<String>())
                    .and(AUTO_DISPATCH_MAPPING_NAME, restResourceName).and(USE_AUTO_DISPATCH, true)
                    .and(STATE_DATA_OBJECT_NAME, DATA_OBJECT.resolveClassname(aggregateProtocolClassName))
                    .and(USE_CQRS, context.parameterOf(CQRS, Boolean::valueOf))
                    .addImports(resolveImports(context, autoDispatchParameter, queryStoreProviderName));

    this.dependOn(RouteMethodTemplateData.from(autoDispatchParameter, parameters));
  }

  private Set<String> resolveImports(final CodeGenerationContext context,
                                     final CodeGenerationParameter autoDispatchParameter,
                                     final String queryStoreProviderName) {
    if (!context.parameterOf(CQRS, Boolean::valueOf)) {
      return Collections.emptySet();
    }

    final String queryStoreProviderQualifiedName =
            ContentQuery.findFullyQualifiedClassName(STORE_PROVIDER,
                    queryStoreProviderName, context.contents());

    final String queriesProtocolQualifiedName =
            autoDispatchParameter.retrieveRelatedValue(QUERIES_PROTOCOL);

    return Stream.of(queryStoreProviderQualifiedName, queriesProtocolQualifiedName).collect(toSet());
  }

  @Override
  public void handleDependencyOutcome(final TemplateStandard standard, final String outcome) {
    this.parameters.<List<String>>find(ROUTE_METHODS).add(outcome);
  }

  @Override
  public TemplateParameters parameters() {
    return parameters;
  }

  @Override
  public TemplateStandard standard() {
    return AUTO_DISPATCH_RESOURCE_HANDLER;
  }

  @Override
  public String filename() {
    return standard().resolveFilename(restResourceName, parameters);
  }

}
