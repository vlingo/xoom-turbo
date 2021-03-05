// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.dataobject;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataObjectGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final List<TemplateData> stateDataObjectTemplateData =
                StateDataObjectTemplateData.from(context.parameterOf(Label.PACKAGE),
                        context.parameterOf(Label.LANGUAGE, Language::valueOf),
                        context.parametersOf(Label.AGGREGATE),
                        context.parametersOf(Label.VALUE_OBJECT).collect(Collectors.toList()),
                        context.contents());

        final List<TemplateData> valueDataObjectTemplateData =
                ValueDataObjectTemplateData.from(context.parameterOf(Label.PACKAGE),
                        context.parameterOf(Label.LANGUAGE, Language::valueOf),
                        context.parametersOf(Label.VALUE_OBJECT));

        return Stream.of(stateDataObjectTemplateData, valueDataObjectTemplateData)
                .flatMap(List::stream).collect(Collectors.toList());
    }

}
