// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.template.projections;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.ParameterKey.Defaults.PACKAGE_NAME;
import static io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard.PROJECTION;
import static io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard.PROJECTION_DISPATCHER_PROVIDER;
import static io.vlingo.xoom.turbo.annotation.codegen.template.TemplateParameter.PROJECTION_TO_DESCRIPTION;
import static java.util.stream.Collectors.toList;

public class ProjectionDispatcherProviderTemplateData extends TemplateData {

  private final TemplateParameters parameters;

  public ProjectionDispatcherProviderTemplateData(final ProjectionType projectionType,
                                                  final Stream<CodeGenerationParameter> projectionActors,
                                                  final List<Content> contents) {
    final String packageName =
            ContentQuery.findPackage(PROJECTION, contents);

    final List<ProjectToDescription> projectToDescriptionEntries =
            ProjectToDescription.from(projectionType, projectionActors.collect(toList()));

    this.parameters =
            TemplateParameters.with(PACKAGE_NAME, packageName)
                    .and(PROJECTION_TO_DESCRIPTION, projectToDescriptionEntries);
  }

  @Override
  public TemplateStandard standard() {
    return PROJECTION_DISPATCHER_PROVIDER;
  }

  @Override
  public TemplateParameters parameters() {
    return parameters;
  }


}
