// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.EXCHANGE;
import static io.vlingo.xoom.codegen.template.TemplateParameter.EXCHANGE_NAMES;
import static io.vlingo.xoom.codegen.template.TemplateParameter.INLINE_EXCHANGE_NAMES;

public class ExchangePropertiesTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static TemplateData from(final Stream<CodeGenerationParameter> aggregates) {
        final Stream<String> exchangeNames =
                aggregates.flatMap(aggregate -> aggregate.retrieveAllRelated(EXCHANGE))
                        .map(exchange -> exchange.value).distinct();

        return new ExchangePropertiesTemplateData(exchangeNames);
    }

    private ExchangePropertiesTemplateData(final Stream<String> exchangeNames) {
        this.parameters =
                TemplateParameters.with(EXCHANGE_NAMES, exchangeNames.collect(Collectors.toList()))
                        .and(INLINE_EXCHANGE_NAMES, exchangeNames.collect(Collectors.joining(";")));
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.EXCHANGE_PROPERTIES;
    }
}
