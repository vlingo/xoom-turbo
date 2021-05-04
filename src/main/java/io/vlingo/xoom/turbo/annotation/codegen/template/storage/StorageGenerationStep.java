// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.codegen.template.storage;

import io.vlingo.xoom.turbo.annotation.codegen.template.projections.ProjectionType;
import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateProcessingStep;

import java.util.List;

import static io.vlingo.xoom.turbo.annotation.codegen.template.Label.*;
import static io.vlingo.xoom.turbo.codegen.parameter.ParameterLabel.*;

public class StorageGenerationStep extends TemplateProcessingStep {

  @Override
  protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
    final String basePackage = context.parameterOf(PACKAGE);
    final Boolean useCQRS = context.parameterOf(CQRS, Boolean::valueOf);
    final StorageType storageType = context.parameterOf(STORAGE_TYPE, StorageType::of);
    final ProjectionType projectionType = context.parameterOf(PROJECTION_TYPE, ProjectionType::valueOf);
    return StorageTemplateDataFactory.build(basePackage, context.contents(), storageType, projectionType, useCQRS);
  }

  @Override
  public boolean shouldProcess(final CodeGenerationContext context) {
    return context.parameterOf(STORAGE_TYPE, StorageType::of).isEnabled();
  }
}
