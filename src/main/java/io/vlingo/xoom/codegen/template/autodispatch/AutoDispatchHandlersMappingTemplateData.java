// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.QUERIES;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class AutoDispatchHandlersMappingTemplateData extends TemplateData {

    private final static String PACKAGE_PATTERN = "%s.%s";
    private final static String PARENT_PACKAGE_NAME = "resource";

    private final String aggregateName;
    private final TemplateParameters parameters;

    protected AutoDispatchHandlersMappingTemplateData(final String basePackage,
                                                      final String aggregateProtocolName,
                                                      final Boolean useCQRS,
                                                      final List<TemplateData> queriesTemplateData,
                                                      final List<Content> contents) {
        this.aggregateName = aggregateProtocolName;
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, resolvePackage(basePackage))
                        .and(AGGREGATE_PROTOCOL_NAME, aggregateProtocolName)
                        .and(STATE_NAME, STATE.resolveClassname(aggregateProtocolName))
                        .and(ENTITY_DATA_NAME, ENTITY_DATA.resolveClassname(aggregateProtocolName))
                        .and(QUERIES_NAME, QUERIES.resolveClassname(aggregateProtocolName)).and(USE_CQRS, useCQRS)
                        .and(QUERY_ALL_METHOD_NAME, findQueryMethodName(aggregateProtocolName, queriesTemplateData))
                        .and(AUTO_DISPATCH_HANDLERS_MAPPING_NAME, standard().resolveClassname(aggregateProtocolName))
                        .addImports(resolveImports(aggregateProtocolName, contents));
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

    private String findQueryMethodName(final String aggregateName,
                                       final List<TemplateData> queriesTemplateData) {
        if(queriesTemplateData.isEmpty()) {
            return null;
        }

        final String expectedQueriesName =
                QUERIES.resolveClassname(aggregateName);

        final Predicate<TemplateParameters> filter =
                parameters -> parameters.hasValue(QUERIES_NAME, expectedQueriesName);

        final Function<TemplateParameters, String> queryMethodNameMapper =
                parameters -> parameters.find(QUERY_ALL_METHOD_NAME);

        return queriesTemplateData.stream().map(TemplateData::parameters).filter(filter)
                .map(queryMethodNameMapper).findFirst().get();
    }

    private Map<TemplateStandard, String> mapClassesWithTemplateStandards(final String aggregateName) {
        return new HashMap<TemplateStandard, String>(){{
            put(AGGREGATE_PROTOCOL, aggregateName);
            put(STATE, STATE.resolveClassname(aggregateName));
            put(QUERIES, QUERIES.resolveClassname(aggregateName));
            put(ENTITY_DATA, ENTITY_DATA.resolveClassname(aggregateName));
        }};
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME).toLowerCase();
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.AUTO_DISPATCH_HANDLERS_MAPPING;
    }

    @Override
    public String filename() {
        return standard().resolveFilename(aggregateName, parameters);
    }

}
