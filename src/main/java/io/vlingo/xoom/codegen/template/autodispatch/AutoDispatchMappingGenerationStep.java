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

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.QUERIES_ACTOR;

public class AutoDispatchMappingGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final String basePackage = context.parameterOf(PACKAGE);
        final Boolean useCQRS = context.parameterOf(CQRS, Boolean::valueOf);
        final String restResourcesData = context.parameterOf(REST_RESOURCES);
        final List<TemplateData> queriesTemplateData = context.templateParametersOf(QUERIES_ACTOR);
        return AutoDispatchMappingTemplateData.build(basePackage, restResourcesData, useCQRS,
                queriesTemplateData, context.contents());
    }

    @Override
    public boolean shouldProcess(final CodeGenerationContext context) {
        return context.hasParameter(USE_AUTO_DISPATCH) && context.hasParameter(REST_RESOURCES) &&
                context.parameterOf(USE_AUTO_DISPATCH, Boolean::valueOf);
    }

}