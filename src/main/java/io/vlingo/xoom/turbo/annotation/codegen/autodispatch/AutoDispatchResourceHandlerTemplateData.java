// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.autodispatch;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.turbo.annotation.codegen.AnnotationBasedTemplateStandard;
import io.vlingo.xoom.turbo.annotation.codegen.Label;
import io.vlingo.xoom.turbo.annotation.codegen.TemplateParameter;
import io.vlingo.xoom.turbo.annotation.codegen.storage.Model;
import io.vlingo.xoom.turbo.annotation.codegen.storage.Queries;
import io.vlingo.xoom.turbo.annotation.codegen.storage.StorageType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.ParameterKey.Defaults.PACKAGE_NAME;
import static io.vlingo.xoom.turbo.annotation.codegen.TemplateParameter.COMPOSITE_ID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class AutoDispatchResourceHandlerTemplateData extends TemplateData {

  private final String restResourceName;
  private final TemplateParameters parameters;

  public static List<TemplateData> from(final CodeGenerationContext context) {
    final CodeElementFormatter codeElementFormatter =
            ComponentRegistry.withType(CodeElementFormatter.class);

    return context.parametersOf(Label.AUTO_DISPATCH_NAME)
            .map(param -> new AutoDispatchResourceHandlerTemplateData(codeElementFormatter, context, param))
            .collect(toList());
  }

  @SuppressWarnings("unchecked")
  private AutoDispatchResourceHandlerTemplateData(final CodeElementFormatter codeElementFormatter,
                                                  final CodeGenerationContext context,
                                                  final CodeGenerationParameter autoDispatchParameter) {
    this.restResourceName = codeElementFormatter.simpleNameOf(autoDispatchParameter.value);

    final String uriRoot =
            autoDispatchParameter.retrieveRelatedValue(Label.URI_ROOT);

    final TemplateParameters queryStoreProviderParameters =
            TemplateParameters.with(TemplateParameter.STORAGE_TYPE, StorageType.STATE_STORE).and(TemplateParameter.MODEL, Model.QUERY);

    final String queryStoreProviderName =
            AnnotationBasedTemplateStandard.STORE_PROVIDER.resolveClassname(queryStoreProviderParameters);

    final String aggregateProtocolClassName =
            codeElementFormatter.simpleNameOf(autoDispatchParameter.retrieveRelatedValue(Label.MODEL_PROTOCOL));

    this.parameters =
            TemplateParameters.with(PACKAGE_NAME, codeElementFormatter.packageOf(autoDispatchParameter.value))
                    .and(TemplateParameter.QUERIES, Queries.from(autoDispatchParameter)).and(TemplateParameter.URI_ROOT, uriRoot)
                    .and(TemplateParameter.STATE_NAME, AnnotationBasedTemplateStandard.AGGREGATE_STATE.resolveClassname(aggregateProtocolClassName))
                    .and(TemplateParameter.REST_RESOURCE_NAME, standard().resolveClassname(restResourceName))
                    .and(TemplateParameter.LOCATION_PATH, PathFormatter.addTrailingSlash(uriRoot))
                    .and(TemplateParameter.ROUTE_DECLARATIONS, RouteDeclaration.from(autoDispatchParameter))
                    .and(TemplateParameter.MODEL_PROTOCOL, autoDispatchParameter.retrieveRelatedValue(Label.MODEL_PROTOCOL))
                    .and(TemplateParameter.MODEL_ACTOR, autoDispatchParameter.retrieveRelatedValue(Label.MODEL_ACTOR))
                    .and(TemplateParameter.HANDLERS_CONFIG_NAME, autoDispatchParameter.retrieveRelatedValue(Label.HANDLERS_CONFIG_NAME))
                    .and(TemplateParameter.STORE_PROVIDER_NAME, queryStoreProviderName).and(TemplateParameter.ROUTE_METHODS, new ArrayList<String>())
                    .and(TemplateParameter.AUTO_DISPATCH_MAPPING_NAME, restResourceName).and(TemplateParameter.USE_AUTO_DISPATCH, true)
                    .and(TemplateParameter.STATE_DATA_OBJECT_NAME, AnnotationBasedTemplateStandard.DATA_OBJECT.resolveClassname(aggregateProtocolClassName))
                    .and(TemplateParameter.USE_CQRS, context.parameterOf(Label.CQRS, Boolean::valueOf))
                    .and(COMPOSITE_ID, AggregateDetail.resolveCompositeIdFields(autoDispatchParameter))
                    .addImports(resolveImports(context, autoDispatchParameter, queryStoreProviderName));

    this.dependOn(RouteMethodTemplateData.from(autoDispatchParameter, parameters));
  }

  private Set<String> resolveImports(final CodeGenerationContext context,
                                     final CodeGenerationParameter autoDispatchParameter,
                                     final String queryStoreProviderName) {
    if (!context.parameterOf(Label.CQRS, Boolean::valueOf)) {
      return Collections.emptySet();
    }

    final String queryStoreProviderQualifiedName =
            ContentQuery.findFullyQualifiedClassName(AnnotationBasedTemplateStandard.STORE_PROVIDER,
                    queryStoreProviderName, context.contents());

    final String queriesProtocolQualifiedName =
            autoDispatchParameter.retrieveRelatedValue(Label.QUERIES_PROTOCOL);

    return Stream.of(queryStoreProviderQualifiedName, queriesProtocolQualifiedName).collect(toSet());
  }

  @Override
  public void handleDependencyOutcome(final TemplateStandard standard, final String outcome) {
    this.parameters.<List<String>>find(TemplateParameter.ROUTE_METHODS).add(outcome);
  }

  @Override
  public TemplateParameters parameters() {
    return parameters;
  }

  @Override
  public TemplateStandard standard() {
    return AnnotationBasedTemplateStandard.AUTO_DISPATCH_RESOURCE_HANDLER;
  }

  @Override
  public String filename() {
    return standard().resolveFilename(restResourceName, parameters);
  }

}
