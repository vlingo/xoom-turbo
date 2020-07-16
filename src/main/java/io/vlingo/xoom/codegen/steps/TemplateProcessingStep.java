// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.steps;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeTemplateProcessor;
import io.vlingo.xoom.codegen.TemplateData;

import java.util.List;

public abstract class TemplateProcessingStep implements CodeGenerationStep {

    @Override
    public void process(final CodeGenerationContext context) {
        buildTemplatesData(context).forEach(templateData -> {
            final String code = CodeTemplateProcessor.instance().process(templateData);
            context.addContent(templateData.standard(), templateData.file(), code);
        });
    }

    protected abstract List<TemplateData> buildTemplatesData(final CodeGenerationContext context);

}
