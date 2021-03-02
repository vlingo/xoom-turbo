// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationStep;
import io.vlingo.xoom.codegen.language.Language;

import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.LANGUAGE;

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
        return context.hasParameter(LANGUAGE) ? context.parameterOf(LANGUAGE, Language::valueOf) : Language.findDefault();
    }

}
