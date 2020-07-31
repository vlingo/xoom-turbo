// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.projections;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.vlingo.xoom.codegen.template.TemplateStandard.PROJECTION;

public class ProjectToDescriptionParameter {

    private static final String FIRST_BECAUSE_OF_PLACEHOLDER = "\"%s name here\"";
    private static final String SECOND_BECAUSE_OF_PLACEHOLDER = "\"Another %s name here\"";
    private static final String PROJECT_TO_DESCRIPTION_BUILD_PATTERN = "ProjectToDescription.with(%s.class, %s)%s";

    private final String joinedTypes;
    private final boolean lastParameter;
    private final String projectionClassName;

    public static List<ProjectToDescriptionParameter> from(final List<String> projectionNames,
                                                           final List<String> projectablesPerProjection)  {
        return IntStream.range(0, projectionNames.size()).mapToObj(index ->
            new ProjectToDescriptionParameter(index, projectionNames.size(),
                    projectionNames.get(index), projectablesPerProjection.get(index))
        ).collect(Collectors.toList());
    }

    public static List<ProjectToDescriptionParameter> from(final ProjectionType projectionType,
                                                           final List<String> aggregateProtocols) {
        return IntStream.range(0, aggregateProtocols.size()).mapToObj(index -> {
            final String projectionName =
                    PROJECTION.resolveClassname(aggregateProtocols.get(index));

            final String becauseOf =
                    String.format(FIRST_BECAUSE_OF_PLACEHOLDER, projectionType.sourceName) + ", " +
                            String.format(SECOND_BECAUSE_OF_PLACEHOLDER, projectionType.sourceName);

            return new ProjectToDescriptionParameter(index, aggregateProtocols.size(), projectionName, becauseOf);
        }).collect(Collectors.toList());
    }

    private ProjectToDescriptionParameter(final int index,
                                          final int numberOfProtocols,
                                          final String projectionClassName,
                                          final String sourceTypes) {
        this.joinedTypes = sourceTypes;
        this.projectionClassName = projectionClassName;
        this.lastParameter = index == numberOfProtocols - 1;
    }

    public String getInitializationCommand() {
        final String separator = lastParameter ? "" : ",";
        return String.format(PROJECT_TO_DESCRIPTION_BUILD_PATTERN, projectionClassName, joinedTypes, separator);
    }

}
