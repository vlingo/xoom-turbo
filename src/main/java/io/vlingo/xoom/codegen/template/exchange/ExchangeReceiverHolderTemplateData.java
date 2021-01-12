// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;
import static io.vlingo.xoom.codegen.template.TemplateStandard.EXCHANGE_RECEIVER_HOLDER;

public class ExchangeReceiverHolderTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final String exchangePackage,
                                          final Stream<CodeGenerationParameter> aggregates,
                                          final List<Content> contents) {
        return aggregates.flatMap(aggregate -> aggregate.retrieveAllRelated(Label.EXCHANGE))
                .filter(exchange -> exchange.retrieveOneRelated(Label.ROLE).value.equalsIgnoreCase("consumer"))
                .map(exchange -> new ExchangeReceiverHolderTemplateData(exchangePackage, exchange, contents))
                .collect(Collectors.toList());
    }

    private ExchangeReceiverHolderTemplateData(final String exchangePackage,
                                               final CodeGenerationParameter exchange,
                                               final List<Content> contents) {
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, exchangePackage)
                        .and(AGGREGATE_PROTOCOL_NAME, exchange.parent().value)
                        .and(EXCHANGE_RECEIVERS, ExchangeReceiversParameter.from(exchange))
                        .addImport(resolveLocalTypeImport(exchange.parent(), contents))
                        .andResolve(EXCHANGE_RECEIVER_HOLDER_NAME, params -> standard().resolveClassname(params));
    }

    private String resolveLocalTypeImport(final CodeGenerationParameter aggregate,
                                          final List<Content> contents) {
        final String dataObjectName = DATA_OBJECT.resolveClassname(aggregate.value);
        return ContentQuery.findFullyQualifiedClassName(DATA_OBJECT, dataObjectName, contents);
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return EXCHANGE_RECEIVER_HOLDER;
    }

}
