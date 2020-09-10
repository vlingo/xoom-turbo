// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.storage.Model;
import io.vlingo.xoom.codegen.template.storage.QueriesParameter;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.AUTO_DISPATCH_NAME;
import static io.vlingo.xoom.codegen.parameter.Label.QUERIES_PROTOCOL;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AUTO_DISPATCH_RESOURCE_HANDLER;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STORE_PROVIDER;
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

    private AutoDispatchResourceHandlerTemplateData(final CodeGenerationContext context,
                                                    final CodeGenerationParameter autoDispatchParameter) {
        this.restResourceName =
                resolveAutoDispatchClassName(autoDispatchParameter.value);

        final TemplateParameters queryStoreProviderParameters =
                TemplateParameters.with(STORAGE_TYPE, StorageType.STATE_STORE).and(MODEL, Model.QUERY);

        final String queryStoreProviderName =
                STORE_PROVIDER.resolveClassname(queryStoreProviderParameters);

        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, resolveAutoDispatchPackage(autoDispatchParameter.value))
                        .and(REST_RESOURCE_NAME, standard().resolveClassname(restResourceName))
                        .and(QUERIES, QueriesParameter.from(autoDispatchParameter))
                        .and(URI_ROOT, autoDispatchParameter.relatedParameterValueOf(Label.URI_ROOT))
                        .and(ROUTE_DECLARATIONS, RouteDeclarationParameter.from(autoDispatchParameter))
                        .and(MODEL_PROTOCOL, autoDispatchParameter.relatedParameterValueOf(Label.MODEL_PROTOCOL))
                        .and(MODEL_ACTOR, autoDispatchParameter.relatedParameterValueOf(Label.MODEL_ACTOR))
                        .and(STORE_PROVIDER_NAME, queryStoreProviderName).and(ROUTE_METHODS, new ArrayList<String>())
                        .and(AUTO_DISPATCH_MAPPING_NAME, restResourceName).and(USE_AUTO_DISPATCH, true)
                        .addImports(resolveImports(context, autoDispatchParameter, queryStoreProviderName));

        this.dependOn(RouteMethodTemplateData.from(autoDispatchParameter, parameters));
    }

    private Set<String> resolveImports(final CodeGenerationContext context,
                                       final CodeGenerationParameter autoDispatchParameter,
                                       final String queryStoreProviderName) {
        final String queryStoreProviderQualifiedName =
                ContentQuery.findFullyQualifiedClassName(STORE_PROVIDER,
                        queryStoreProviderName, context.contents());

        final String queriesProtocolQualifiedName =
                autoDispatchParameter.relatedParameterValueOf(QUERIES_PROTOCOL);

        return Stream.of(queryStoreProviderQualifiedName, queriesProtocolQualifiedName).collect(toSet());
    }

    @Override
    public void handleDependencyOutcome(final TemplateStandard standard, final String outcome) {
        this.parameters.<List<String>>find(ROUTE_METHODS).add(outcome);
    }

    private String resolveAutoDispatchClassName(final String qualifiedName) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    }

    private String resolveAutoDispatchPackage(final String qualifiedName) {
        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
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
