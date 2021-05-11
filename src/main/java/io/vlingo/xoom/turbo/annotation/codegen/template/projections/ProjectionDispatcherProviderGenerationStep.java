// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.template.projections;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.turbo.annotation.codegen.template.AnnotationBasedTemplateStandard.PROJECTION;
import static io.vlingo.xoom.turbo.annotation.codegen.template.Label.PROJECTION_ACTOR;
import static io.vlingo.xoom.turbo.annotation.codegen.template.Label.PROJECTION_TYPE;

public class ProjectionDispatcherProviderGenerationStep extends TemplateProcessingStep {

  @Override
  protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
    final ProjectionType projectionType =
            context.parameterOf(PROJECTION_TYPE, ProjectionType::valueOf);

    final TemplateData projectionDispatcherProviderTemplateData =
            new ProjectionDispatcherProviderTemplateData(projectionType,
                    context.parametersOf(PROJECTION_ACTOR), context.contents());

    return Arrays.asList(projectionDispatcherProviderTemplateData);
  }

  @Override
  public boolean shouldProcess(final CodeGenerationContext context) {
    return ContentQuery.exists(PROJECTION, context.contents());
  }
}
