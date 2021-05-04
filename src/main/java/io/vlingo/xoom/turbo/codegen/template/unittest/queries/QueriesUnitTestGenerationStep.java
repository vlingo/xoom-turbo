// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.template.unittest.queries;

import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.designer.Label;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.codegen.template.TemplateProcessingStep;

import java.util.List;
import java.util.stream.Collectors;

public class QueriesUnitTestGenerationStep extends TemplateProcessingStep {

  @Override
  protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
    final List<CodeGenerationParameter> aggregates =
            context.parametersOf(Label.AGGREGATE).collect(Collectors.toList());

    final List<CodeGenerationParameter> valueObjects =
            context.parametersOf(Label.VALUE_OBJECT).collect(Collectors.toList());

    return QueriesUnitTestTemplateData.from(context.contents(), aggregates, valueObjects);
  }

  @Override
  public boolean shouldProcess(final CodeGenerationContext context) {
    return context.parameterOf(Label.CQRS, Boolean::valueOf);
  }
}
