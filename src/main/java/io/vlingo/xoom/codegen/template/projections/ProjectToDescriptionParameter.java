// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.projections;


import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.vlingo.xoom.codegen.template.TemplateStandard.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class ProjectToDescriptionParameter {

    private static final String FIRST_BECAUSE_OF_PLACEHOLDER = "\"%s name here\"";
    private static final String SECOND_BECAUSE_OF_PLACEHOLDER = "\"Another %s name here\"";
    private static final String PROJECT_TO_DESCRIPTION_BUILD_PATTERN = "ProjectToDescription.with(%s.class, %s)%s";

    private final String joinedTypes;
    private final boolean lastParameter;
    private final String projectionClassName;

    public static List<ProjectToDescriptionParameter> from(final List<CodeGenerationParameter> projectionActors)  {
        return IntStream.range(0, projectionActors.size()).mapToObj(index ->{
                final CodeGenerationParameter projectionActor = projectionActors.get(index);
                final Set<String> eventNames = projectionActor.retrieveAllRelated(Label.SOURCE)
                        .map(source -> source.value).map(String::trim).collect(toSet());
                return new ProjectToDescriptionParameter(index, projectionActors.size(), projectionActor.value, eventNames);
        }).collect(toList());
    }

    public static List<ProjectToDescriptionParameter> from(final ProjectionType projectionType,
                                                           final List<Content> contents) {
        final Set<String> aggregateProtocols =
                ContentQuery.findClassNames(AGGREGATE_PROTOCOL, contents);

        final Iterator<String> iterator = aggregateProtocols.iterator();

        return IntStream.range(0, aggregateProtocols.size()).mapToObj(index -> {
            final String aggregateProtocol = iterator.next();

            final String projectionName =
                    PROJECTION.resolveClassname(aggregateProtocol);

            final String becauseOf =
                    buildCauseTypesExpression(aggregateProtocol, projectionType, contents);

            return new ProjectToDescriptionParameter(index, aggregateProtocols.size(), projectionName, becauseOf);
        }).collect(toList());
    }

    private ProjectToDescriptionParameter(final int index,
                                          final int numberOfProtocols,
                                          final String projectionClassName,
                                          final Set<String> eventNames) {
        this(index, numberOfProtocols, projectionClassName, formatEventNames(eventNames));
    }

    private ProjectToDescriptionParameter(final int index,
                                          final int numberOfProtocols,
                                          final String projectionClassName,
                                          final String joinedTypes) {
        this.projectionClassName = projectionClassName;
        this.lastParameter = index == numberOfProtocols - 1;
        this.joinedTypes = joinedTypes;
    }

    private static String buildCauseTypesExpression(final String aggregateProtocol,
                                                    final ProjectionType projectionType,
                                                    final List<Content> contents) {
        final String protocolPackage =
                ContentQuery.findPackage(AGGREGATE_PROTOCOL, aggregateProtocol, contents);

        final Set<String> eventNames =
                ContentQuery.findClassNames(DOMAIN_EVENT, protocolPackage, contents);

        if(eventNames.isEmpty()) {
            return String.format(FIRST_BECAUSE_OF_PLACEHOLDER, projectionType.sourceName) + ", " +
                    String.format(SECOND_BECAUSE_OF_PLACEHOLDER, projectionType.sourceName);
        }

        return formatEventNames(eventNames);
    }

    private static String formatEventNames(final Set<String> eventNames) {
        return eventNames.stream().map(s -> s + ".class.getName()").collect(Collectors.joining(", "));
    }

    public String getInitializationCommand() {
        final String separator = lastParameter ? "" : ",";
        return String.format(PROJECT_TO_DESCRIPTION_BUILD_PATTERN, projectionClassName, joinedTypes, separator);
    }

}
