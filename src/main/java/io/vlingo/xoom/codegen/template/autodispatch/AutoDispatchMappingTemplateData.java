// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.beans.Introspector;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.QUERIES;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class AutoDispatchMappingTemplateData extends TemplateData {

    private final static String PACKAGE_PATTERN = "%s.%s";
    private static final String REST_RESOURCES_SEPARATOR = ";";
    private final static String PARENT_PACKAGE_NAME = "resource";

    private final String aggregateName;
    private final TemplateParameters parameters;

    public static List<TemplateData> build(final String basePackage,
                                           final String restResourcesData,
                                           final Boolean useCQRS,
                                           final List<TemplateData> queriesTemplateData,
                                           final List<Content> contents) {
        final Function<String, AutoDispatchMappingTemplateData> mapper =
                aggregateName -> new AutoDispatchMappingTemplateData(basePackage,
                        aggregateName, useCQRS, contents, queriesTemplateData);

        return Arrays.asList(restResourcesData.split(REST_RESOURCES_SEPARATOR))
                .stream().map(mapper).collect(Collectors.toList());
    }

    private AutoDispatchMappingTemplateData(final String basePackage,
                                            final String aggregateProtocolName,
                                            final Boolean useCQRS,
                                            final List<Content> contents,
                                            final List<TemplateData> queriesTemplateData) {
        this.aggregateName = aggregateProtocolName;
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, resolvePackage(basePackage))
                        .and(IMPORTS, resolveImports(aggregateProtocolName, contents))
                        .and(AUTO_DISPATCH_MAPPING_NAME, AUTO_DISPATCH_MAPPING.resolveClassname(aggregateProtocolName))
                        .and(AGGREGATE_PROTOCOL_NAME, aggregateProtocolName)
                        .and(ENTITY_NAME, AGGREGATE.resolveClassname(aggregateProtocolName))
                        .and(ENTITY_DATA_NAME, ENTITY_DATA.resolveClassname(aggregateProtocolName))
                        .and(QUERY_ALL_METHOD_NAME, findQueryMethodName(aggregateProtocolName, queriesTemplateData))
                        .and(URI_ROOT, resolveUriRoot(aggregateProtocolName))
                        .and(USE_CQRS, useCQRS);
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME).toLowerCase();
    }

    private String resolveUriRoot(final String aggregateProtocolName) {
        final String formatted = Introspector.decapitalize(aggregateProtocolName);
        return formatted.endsWith("s") ? formatted : formatted + "s";
    }

    private Set<ImportParameter> resolveImports(final String aggregateName,
                                                final List<Content> contents) {
        final Map<TemplateStandard, String> classes =
                mapClassesWithTemplateStandards(aggregateName);

        return classes.entrySet().stream().flatMap(entry -> {
            try {
                final String qualifiedName =
                        ContentQuery.findFullyQualifiedClassName(entry.getKey(),
                                entry.getValue(), contents);

                return ImportParameter.of(qualifiedName).stream();
            } catch (final IllegalArgumentException exception) {
                return Stream.empty();
            }
        }).collect(Collectors.toSet());
    }

    private String findQueryMethodName(final String aggregateName,
                                       final List<TemplateData> queriesTemplateData) {
        if(queriesTemplateData.isEmpty()) {
            return null;
        }

        final String expectedQueriesName =
                QUERIES.resolveClassname(aggregateName);

        final Function<TemplateData, TemplateParameters> parametersMapper =
                templateData -> templateData.parameters();

        final Predicate<TemplateParameters> filter =
                parameters -> parameters.hasValue(QUERIES_NAME, expectedQueriesName);

        final Function<TemplateParameters, String> queryMethodNameMapper =
                parameters -> parameters.find(QUERY_ALL_METHOD_NAME);

        return queriesTemplateData.stream().map(parametersMapper).filter(filter)
                .map(queryMethodNameMapper).findFirst().get();
    }

    private Map<TemplateStandard, String> mapClassesWithTemplateStandards(final String aggregateName) {
        return new HashMap<TemplateStandard, String>(){{
            put(AGGREGATE_PROTOCOL, aggregateName);
            put(QUERIES, QUERIES.resolveClassname(aggregateName));
            put(QUERIES_ACTOR, QUERIES_ACTOR.resolveClassname(aggregateName));
            put(ENTITY_DATA, ENTITY_DATA.resolveClassname(aggregateName));
        }};
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
