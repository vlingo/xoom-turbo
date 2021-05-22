// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.annotation.codegen.storage;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;
import io.vlingo.xoom.turbo.annotation.codegen.Label;
import io.vlingo.xoom.turbo.annotation.codegen.projections.ProjectionType;

import java.util.List;

public class StorageGenerationStep extends TemplateProcessingStep {

  @Override
  protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
    final String basePackage = context.parameterOf(Label.PACKAGE);
    final Boolean useCQRS = context.parameterOf(Label.CQRS, Boolean::valueOf);
    final StorageType storageType = context.parameterOf(Label.STORAGE_TYPE, StorageType::of);
    final ProjectionType projectionType = context.parameterOf(Label.PROJECTION_TYPE, ProjectionType::valueOf);
    return StorageTemplateDataFactory.build(basePackage, context.contents(), storageType, projectionType, useCQRS);
  }

  @Override
  public boolean shouldProcess(final CodeGenerationContext context) {
    return context.parameterOf(Label.STORAGE_TYPE, StorageType::of).isEnabled();
  }
}
