// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.schemata;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.AGGREGATE;
import static io.vlingo.xoom.codegen.parameter.Label.EXCHANGE;

public class SchemataGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final List<CodeGenerationParameter> exchanges =
                context.parametersOf(AGGREGATE).flatMap(aggregate -> aggregate.retrieveAllRelated(EXCHANGE))
                .collect(Collectors.toList());

        final List<TemplateData> templateData = new ArrayList<>();
        templateData.addAll(SchemataSpecificationTemplateData.from(exchanges));
        templateData.add(new SchemataPluginTemplateData(exchanges));
        return templateData;
    }

    @Override
    public boolean shouldProcess(CodeGenerationContext context) {
        return context.parametersOf(AGGREGATE).flatMap(aggregate -> aggregate.retrieveAllRelated(EXCHANGE)).count() > 0;
    }
}
