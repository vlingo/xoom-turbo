// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;

import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.USE_AUTO_DISPATCH;
import static io.vlingo.xoom.codegen.template.TemplateStandard.QUERIES_ACTOR;

public class AutoDispatchMappingGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final List<TemplateData> queriesTemplateData = context.templateParametersOf(QUERIES_ACTOR);
        return AutoDispatchMappingTemplateDataFactory.build(context.parameters(),
                queriesTemplateData, context.contents());
    }

    @Override
    public boolean shouldProcess(final CodeGenerationContext context) {
        return context.hasParameter(USE_AUTO_DISPATCH) &&
                context.parameterOf(USE_AUTO_DISPATCH, Boolean::valueOf);
    }

}