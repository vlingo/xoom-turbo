// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template;

import io.vlingo.xoom.turbo.codegen.CodeGenerationContext;
import io.vlingo.xoom.turbo.codegen.CodeGenerationStep;
import io.vlingo.xoom.turbo.codegen.language.Language;

import java.util.List;

public abstract class TemplateProcessingStep implements CodeGenerationStep {

  @Override
  public void process(final CodeGenerationContext context) {
    final Language language = resolveLanguage(context);
    language.resolvePreParametersProcessing(context.parameters());
    buildTemplatesData(context).forEach(templateData -> {
      language.resolvePostParametersProcessing(templateData.parameters());
      final String code = TemplateProcessor.instance().process(language, templateData);
      context.registerTemplateProcessing(language, templateData, code);
    });
  }

  protected abstract List<TemplateData> buildTemplatesData(final CodeGenerationContext context);

  protected Language resolveLanguage(final CodeGenerationContext context) {
    return Language.findDefault();
  }

}
