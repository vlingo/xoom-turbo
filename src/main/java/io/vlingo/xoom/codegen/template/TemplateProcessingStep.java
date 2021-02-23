// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationStep;

import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.LANGUAGE;

public abstract class TemplateProcessingStep implements CodeGenerationStep {

    @Override
    public void process(final CodeGenerationContext context) {
        buildTemplatesData(context).forEach(templateData -> {
            final Language language = resolveLanguage(context);
            final String code = TemplateProcessor.instance().process(language, templateData);
            context.registerTemplateProcessing(language, templateData, code);
        });
    }

    protected abstract List<TemplateData> buildTemplatesData(final CodeGenerationContext context);

    protected Language resolveLanguage(final CodeGenerationContext context) {
        return context.hasParameter(LANGUAGE) ? context.parameterOf(LANGUAGE, Language::valueOf) : Language.findDefault();
    }

}
