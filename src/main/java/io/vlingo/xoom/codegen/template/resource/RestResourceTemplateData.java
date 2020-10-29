// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.autodispatch.AutoDispatchRouteTemplateData;
import io.vlingo.xoom.codegen.template.storage.Model;
import io.vlingo.xoom.codegen.template.storage.QueriesParameter;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static io.vlingo.xoom.codegen.content.ContentQuery.findFullyQualifiedClassName;
import static io.vlingo.xoom.codegen.parameter.Label.ROUTE_SIGNATURE;
import static io.vlingo.xoom.codegen.template.TemplateParameter.QUERIES;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class RestResourceTemplateData extends TemplateData {

    private final static String PACKAGE_PATTERN = "%s.%s";
    private final static String PARENT_PACKAGE_NAME = "resource";

    private final String packageName;
    private final String aggregateName;
    private final TemplateParameters parameters;

    public RestResourceTemplateData(final String basePackage,
                                    final CodeGenerationParameter aggregateParameter,
                                    final List<Content> contents,
                                    final Boolean useCQRS) {
        this.aggregateName = aggregateParameter.value;
        this.packageName = resolvePackage(basePackage);
        this.parameters = loadParameters(aggregateParameter, contents, useCQRS);
        this.loadDependencies(aggregateParameter, useCQRS);
    }

    private TemplateParameters loadParameters(final CodeGenerationParameter aggregateParameter,
                                              final List<Content> contents,
                                              final Boolean useCQRS) {
        final QueriesParameter queriesParameter = QueriesParameter.from(aggregateParameter, contents);

        final Function<TemplateParameters, Object> modelProtocolResolver =
                params -> requireModelTypes(aggregateParameter) ? aggregateName : "";

        return TemplateParameters.with(REST_RESOURCE_NAME, REST_RESOURCE.resolveClassname(aggregateName))
                .and(QUERIES, queriesParameter).and(PACKAGE_NAME, packageName).and(USE_CQRS, useCQRS)
                .addImports(resolveImports(aggregateParameter, queriesParameter, contents, useCQRS))
                .and(URI_ROOT, aggregateParameter.relatedParameterValueOf(Label.URI_ROOT))
                .and(ROUTE_DECLARATIONS, RouteDeclarationParameter.from(aggregateParameter))
                .and(MODEL_ACTOR, AGGREGATE.resolveClassname(aggregateName))
                .and(STORE_PROVIDER_NAME, resolveQueryStoreProviderName())
                .andResolve(MODEL_PROTOCOL, modelProtocolResolver)
                .and(ROUTE_METHODS, new ArrayList<String>());
    }

    private void loadDependencies(final CodeGenerationParameter aggregate, final boolean useCQRS) {
        if(useCQRS) {
            aggregate.relate(RouteDetail.defaultQueryRouteParameter());
        }
        this.dependOn(AutoDispatchRouteTemplateData.from(aggregate.retrieveAll(ROUTE_SIGNATURE)));
    }

    private Set<String> resolveImports(final CodeGenerationParameter aggregateParameter,
                                       final QueriesParameter queriesParameter,
                                       final List<Content> contents,
                                       final Boolean useCQRS) {
        final Set<String> imports = new HashSet<>();
        if(requireModelTypes(aggregateParameter)) {
            final String aggregateEntityName = AGGREGATE.resolveClassname(aggregateName);
            imports.add(findFullyQualifiedClassName(AGGREGATE_PROTOCOL, aggregateName, contents));
            imports.add(findFullyQualifiedClassName(AGGREGATE, aggregateEntityName, contents));
        }
        if(useCQRS) {
            imports.addAll(queriesParameter.getQualifiedNames());
        }
        return imports;
    }

    private String resolveQueryStoreProviderName() {
        final TemplateParameters queryStoreProviderParameters =
                TemplateParameters.with(STORAGE_TYPE, StorageType.STATE_STORE)
                        .and(MODEL, Model.QUERY);

        return STORE_PROVIDER.resolveClassname(queryStoreProviderParameters);
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME);
    }

    private boolean requireModelTypes(final CodeGenerationParameter aggregateParameter) {
        return RouteDetail.requireEntityLoad(aggregateParameter);
    }

    @Override
    public void handleDependencyOutcome(final TemplateStandard standard, final String outcome) {
        this.parameters.<List<String>>find(ROUTE_METHODS).add(outcome);
    }

    @Override
    public TemplateStandard standard() {
        return REST_RESOURCE;
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public String filename() {
        return standard().resolveFilename(aggregateName, parameters);
    }

}