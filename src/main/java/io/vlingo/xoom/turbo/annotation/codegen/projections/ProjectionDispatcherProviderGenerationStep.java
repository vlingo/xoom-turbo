// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.projections;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;
import io.vlingo.xoom.turbo.annotation.codegen.AnnotationBasedTemplateStandard;
import io.vlingo.xoom.turbo.annotation.codegen.Label;

import java.util.Arrays;
import java.util.List;

public class ProjectionDispatcherProviderGenerationStep extends TemplateProcessingStep {

  @Override
  protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
    final ProjectionType projectionType =
            context.parameterOf(Label.PROJECTION_TYPE, ProjectionType::valueOf);

    final TemplateData projectionDispatcherProviderTemplateData =
            new ProjectionDispatcherProviderTemplateData(projectionType,
                    context.parametersOf(Label.PROJECTION_ACTOR), context.contents());

    return Arrays.asList(projectionDispatcherProviderTemplateData);
  }

  @Override
  public boolean shouldProcess(final CodeGenerationContext context) {
    return ContentQuery.exists(AnnotationBasedTemplateStandard.PROJECTION, context.contents());
  }
}
