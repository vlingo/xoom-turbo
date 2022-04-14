// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.autodispatch;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;
import io.vlingo.xoom.turbo.annotation.codegen.Label;

import java.util.List;

public class AutoDispatchResourceHandlerGenerationStep extends TemplateProcessingStep {

  @Override
  protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
    return AutoDispatchResourceHandlerTemplateData.from(context);
  }

  @Override
  public boolean shouldProcess(final CodeGenerationContext context) {
    return context.hasParameter(Label.USE_AUTO_DISPATCH) &&
            context.parameterOf(Label.USE_AUTO_DISPATCH, Boolean::valueOf);
  }

}