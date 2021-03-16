// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.projectgenerationsettings;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.ARTIFACT_ID;
import static io.vlingo.xoom.codegen.parameter.Label.PROJECT_SETTINGS_PAYLOAD;

public class ProjectSettingsGenerationStep extends TemplateProcessingStep {

  @Override
  protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
    final String appName = context.parameterOf(ARTIFACT_ID);
    final String generationSettings = context.parameterOf(PROJECT_SETTINGS_PAYLOAD);
    return Arrays.asList(new ProjectSettingsTemplateData(appName, generationSettings));
  }
}
