// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.codegen.storage;

import io.vlingo.xoom.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.turbo.annotation.codegen.AnnotationBasedTemplateStandard;
import io.vlingo.xoom.turbo.annotation.codegen.Label;

import java.util.*;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.template.ParameterKey.Defaults.PACKAGE_NAME;
import static io.vlingo.xoom.turbo.ComponentRegistry.withType;
import static io.vlingo.xoom.turbo.annotation.codegen.TemplateParameter.QUERIES_ACTOR_NAME;
import static io.vlingo.xoom.turbo.annotation.codegen.TemplateParameter.QUERIES_NAME;

public class Queries {

  private static final String QUALIFIED_NAME_PATTERN = "%s.%s";

  public final String protocolName;
  public final String actorName;
  public final String attributeName;
  public final Set<String> qualifiedNames = new HashSet<>();

  public static List<Queries> from(final Boolean useCQRS,
                                   final List<Content> contents,
                                   final List<TemplateData> templatesData) {
    if (!useCQRS) {
      return Collections.emptyList();
    }

    return from(Model.QUERY, contents, templatesData);
  }

  public static List<Queries> from(final Model model,
                                   final List<Content> contents,
                                   final List<TemplateData> templatesData) {
    if (!model.isQueryModel()) {
      return Collections.emptyList();
    }

    if (ContentQuery.exists(AnnotationBasedTemplateStandard.QUERIES_ACTOR, contents)) {
      return ContentQuery.filterByStandard(AnnotationBasedTemplateStandard.QUERIES_ACTOR, contents)
              .filter(Content::isProtocolBased)
              .map(content -> new Queries(content.retrieveProtocolQualifiedName(),
                      content.retrieveQualifiedName()))
              .collect(Collectors.toList());
    }

    return templatesData.stream().filter(data -> data.hasStandard(AnnotationBasedTemplateStandard.QUERIES_ACTOR))
            .map(data -> new Queries(data.parameters())).collect(Collectors.toList());
  }

  public static Queries from(final CodeGenerationParameter autoDispatchParameter) {
    if (!autoDispatchParameter.hasAny(Label.QUERIES_PROTOCOL)) {
      return Queries.empty();
    }
    return new Queries(autoDispatchParameter.retrieveRelatedValue(Label.QUERIES_PROTOCOL),
            autoDispatchParameter.retrieveRelatedValue(Label.QUERIES_ACTOR));
  }

  public static Queries from(final CodeGenerationParameter aggregateParameter,
                             final List<Content> contents,
                             final Boolean useCQRS) {
    if (!useCQRS) {
      return Queries.empty();
    }

    final String queriesProtocol = AnnotationBasedTemplateStandard.QUERIES.resolveClassname(aggregateParameter.value);
    final String queriesActor = AnnotationBasedTemplateStandard.QUERIES_ACTOR.resolveClassname(aggregateParameter.value);
    return new Queries(ContentQuery.findFullyQualifiedClassName(AnnotationBasedTemplateStandard.QUERIES, queriesProtocol, contents),
            ContentQuery.findFullyQualifiedClassName(AnnotationBasedTemplateStandard.QUERIES_ACTOR, queriesActor, contents));
  }

  private static Queries empty() {
    return new Queries("", "", "", "");
  }

  private Queries(final TemplateParameters parameters) {
    this(parameters.find(PACKAGE_NAME), parameters.find(QUERIES_NAME),
            parameters.find(PACKAGE_NAME), parameters.find(QUERIES_ACTOR_NAME));
  }

  private Queries(final String protocolQualifiedName,
                  final String actorQualifiedName) {
    this(withType(CodeElementFormatter.class).packageOf(protocolQualifiedName), withType(CodeElementFormatter.class).simpleNameOf(protocolQualifiedName),
            withType(CodeElementFormatter.class).packageOf(actorQualifiedName), withType(CodeElementFormatter.class).simpleNameOf(actorQualifiedName));
  }

  private Queries(final String protocolPackageName,
                  final String protocolName,
                  final String actorPackageName,
                  final String actorName) {
    this.actorName = actorName;
    this.protocolName = protocolName;
    this.attributeName = withType(CodeElementFormatter.class).simpleNameToAttribute(protocolName);

    if (!isEmpty()) {
      this.qualifiedNames.addAll(
              Arrays.asList(String.format(QUALIFIED_NAME_PATTERN, protocolPackageName, protocolName),
                      String.format(QUALIFIED_NAME_PATTERN, actorPackageName, actorName)));
    }
  }

  public boolean isEmpty() {
    return protocolName.isEmpty() && actorName.isEmpty() && attributeName.isEmpty();
  }

}
