// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.template.projections;

import io.vlingo.xoom.turbo.annotation.codegen.template.Label;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class ProjectToDescription {

  private static final String DEFAULT_SOURCE_NAME_INVOCATION = ".class.getName()";
  private static final String PROJECT_TO_DESCRIPTION_BUILD_PATTERN = "ProjectToDescription.with(%s.class, %s)%s";

  private final String joinedTypes;
  private final boolean lastParameter;
  private final String projectionClassName;

  public static List<ProjectToDescription> from(final ProjectionType projectionType,
                                                final List<CodeGenerationParameter> projectionActors) {
    return IntStream.range(0, projectionActors.size()).mapToObj(index -> {
      final CodeGenerationParameter projectionActor = projectionActors.get(index);
      final Set<String> eventNames = projectionActor.retrieveAllRelated(Label.SOURCE)
              .map(source -> source.value).map(String::trim).collect(toSet());
      return new ProjectToDescription(index, projectionActors.size(), projectionActor.value, projectionType, eventNames);
    }).collect(toList());
  }

  private ProjectToDescription(final int index,
                               final int numberOfProtocols,
                               final String projectionClassName,
                               final ProjectionType projectionType,
                               final Set<String> sourceNames) {
    this.projectionClassName = projectionClassName;
    this.lastParameter = index == numberOfProtocols - 1;
    this.joinedTypes = joinSourceTypes(projectionType, sourceNames);
  }

  private String joinSourceTypes(final ProjectionType projectionType, final Set<String> sourceNames) {
    final Function<String, String> mapper =
            projectionType.isEventBased() ?
                    sourceName -> sourceName + DEFAULT_SOURCE_NAME_INVOCATION :
                    sourceName -> String.format("\"%s\"", sourceName);

    return sourceNames.stream().map(mapper).collect(Collectors.joining(", "));
  }

  public String getInitializationCommand() {
    final String separator = lastParameter ? "" : ",";
    return String.format(PROJECT_TO_DESCRIPTION_BUILD_PATTERN, projectionClassName, joinedTypes, separator);
  }

}
