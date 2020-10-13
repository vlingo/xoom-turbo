// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.beans.Introspector;
import java.util.*;
import java.util.stream.Collectors;

import static io.vlingo.http.Method.GET;
import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.parameter.Label.ROUTE_METHOD;
import static io.vlingo.xoom.codegen.parameter.Label.ROUTE_SIGNATURE;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.URI_ROOT;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.QUERIES;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.QUERIES_ACTOR;

public class AutoDispatchMappingTemplateData extends TemplateData {

    private final static String PACKAGE_PATTERN = "%s.%s";
    private final static String PARENT_PACKAGE_NAME = "resource";

    private final static CodeGenerationParameter DEFAULT_QUERY_ROUTE_PARAMETER =
            CodeGenerationParameter.of(ROUTE_SIGNATURE, "queryAll")
                    .relate(ROUTE_METHOD, GET).relate(READ_ONLY, "true");

    private final String aggregateName;
    private final TemplateParameters parameters;

    protected AutoDispatchMappingTemplateData(final String basePackage,
                                              final CodeGenerationParameter aggregate,
                                              final Boolean useCQRS,
                                              final List<Content> contents) {

        this.aggregateName = aggregate.value;
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, resolvePackage(basePackage))
                        .and(AGGREGATE_PROTOCOL_NAME, aggregateName)
                        .and(ENTITY_NAME, AGGREGATE.resolveClassname(aggregateName))
                        .and(ENTITY_DATA_NAME, ENTITY_DATA.resolveClassname(aggregateName))
                        .and(QUERIES_NAME, QUERIES.resolveClassname(aggregateName))
                        .and(QUERIES_ACTOR_NAME, QUERIES_ACTOR.resolveClassname(aggregateName))
                        .and(AUTO_DISPATCH_MAPPING_NAME, AUTO_DISPATCH_MAPPING.resolveClassname(aggregateName))
                        .and(AUTO_DISPATCH_HANDLERS_MAPPING_NAME, AUTO_DISPATCH_HANDLERS_MAPPING.resolveClassname(aggregateName))
                        .and(URI_ROOT, aggregate.relatedParameterValueOf(Label.URI_ROOT))
                        .addImports(resolveImports(aggregateName, contents))
                        .and(ROUTE_DECLARATIONS, new ArrayList<String>())
                        .and(USE_CQRS, useCQRS);

        this.loadDependencies(aggregate, useCQRS);
    }

    private void loadDependencies(final CodeGenerationParameter aggregate, final boolean useCQRS) {
        if(useCQRS) {
            aggregate.relate(DEFAULT_QUERY_ROUTE_PARAMETER);
        }
        this.dependOn(AutoDispatchRouteTemplateData.from(aggregate.retrieveAll(ROUTE_SIGNATURE)));
    }

    @Override
    public void handleDependencyOutcome(final TemplateStandard standard, final String outcome) {
        this.parameters.<List<String>>find(ROUTE_DECLARATIONS).add(outcome);
    }

    private Set<String> resolveImports(final String aggregateName,
                                       final List<Content> contents) {
        final Map<TemplateStandard, String> classes =
                mapClassesWithTemplateStandards(aggregateName);

        return classes.entrySet().stream().map(entry -> {
            try {
                final String className = entry.getValue();
                final TemplateStandard standard = entry.getKey();
                return ContentQuery.findFullyQualifiedClassName(standard, className, contents);
            } catch (final IllegalArgumentException exception) {
                return null;
            }
        }).collect(Collectors.toSet());
    }

    private Map<TemplateStandard, String> mapClassesWithTemplateStandards(final String aggregateName) {
        return new HashMap<TemplateStandard, String>(){{
            put(AGGREGATE, AGGREGATE.resolveClassname(aggregateName));
            put(AGGREGATE_PROTOCOL, aggregateName);
            put(QUERIES, QUERIES.resolveClassname(aggregateName));
            put(QUERIES_ACTOR, QUERIES_ACTOR.resolveClassname(aggregateName));
            put(ENTITY_DATA, ENTITY_DATA.resolveClassname(aggregateName));
        }};
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME).toLowerCase();
    }

    private String formatUriRoot(final String aggregateProtocolName) {
        final String formatted = Introspector.decapitalize(aggregateProtocolName);
        return formatted.endsWith("s") ? formatted : formatted + "s";
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return AUTO_DISPATCH_MAPPING;
    }

    @Override
    public String filename() {
        return standard().resolveFilename(aggregateName, parameters);
    }

}
