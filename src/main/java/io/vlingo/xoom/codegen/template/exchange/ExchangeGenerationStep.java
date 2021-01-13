// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;

public class ExchangeGenerationStep extends TemplateProcessingStep {

    private static final String PACKAGE_PATTERN = "%.%.%";

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final List<Content> contents = context.contents();
        final String exchangePackage = resolvePackage(context.parameterOf(PACKAGE));
        final Stream<CodeGenerationParameter> aggregates = context.parametersOf(AGGREGATE);

        return Stream.of(ExchangeMapperTemplateData.from(exchangePackage, aggregates, contents),
                ExchangeReceiverHolderTemplateData.from(exchangePackage, aggregates, contents),
                ExchangeAdapterTemplateData.from(exchangePackage, aggregates, contents),
                Arrays.asList(ExchangePropertiesTemplateData.from(aggregates)))
                .flatMap(templates -> templates.stream())
                .collect(Collectors.toList());
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, "infrastructure", "exchange");
    }

    @Override
    public boolean shouldProcess(final CodeGenerationContext context) {
        return context.hasParameter(AGGREGATE) && context.parametersOf(AGGREGATE).anyMatch(aggregate -> aggregate.hasAny(EXCHANGE));
    }
}
