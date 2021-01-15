// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExchangesParameter {

    private final String exchangeVariableName;
    private final Set<CoveyParameter> coveys;

    public static List<ExchangesParameter> from(final List<CodeGenerationParameter> exchanges) {
        return exchanges.stream().map(exchange -> exchange.value).distinct()
                .map(exchangeName -> new ExchangesParameter(exchangeName, exchanges))
                .collect(Collectors.toList());
    }

    public ExchangesParameter(final String exchangeName,
                              final List<CodeGenerationParameter> allExchangeParameters) {
        this.exchangeVariableName = formatValidVariableName(exchangeName);
        this.coveys = resolveCoveyParameters(exchangeName, allExchangeParameters);
    }

    private Set<CoveyParameter> resolveCoveyParameters(final String exchangeName,
                                                       final List<CodeGenerationParameter> allExchangeParameters) {
        final List<CodeGenerationParameter> relatedExchangeParameters =
                allExchangeParameters.stream().filter(exchange -> exchange.value.equals(exchangeName))
                .collect(Collectors.toList());

        return CoveyParameter.from(relatedExchangeParameters);
    }

    public String getExchangeVariableName() {
        return exchangeVariableName;
    }

    public String getExchangeSettingsName() {
        return exchangeVariableName + "Settings";
    }

    public Set<CoveyParameter> getCoveys() {
        return coveys;
    }

    private String formatValidVariableName(final String exchangeName) {
        boolean shouldUpper = false;
        final StringBuilder formatted = new StringBuilder();
        for(char character : exchangeName.toLowerCase().toCharArray()) {
            if(!Character.isJavaIdentifierPart(character)) {
                shouldUpper = true;
                continue;
            }
            formatted.append(shouldUpper ? String.valueOf(character).toUpperCase() : character);
            shouldUpper = false;
        }
        return formatted.toString();
    }
}
