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
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail;
import io.vlingo.xoom.codegen.template.model.valueobject.ValueObjectDetail;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class ExchangeReceiverHolderTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final String exchangePackage,
                                          final Stream<CodeGenerationParameter> aggregates,
                                          final List<Content> contents) {
        return aggregates.flatMap(aggregate -> aggregate.retrieveAllRelated(EXCHANGE))
                .filter(exchange -> exchange.retrieveRelatedValue(ROLE, ExchangeRole::of).isConsumer())
                .map(exchange -> new ExchangeReceiverHolderTemplateData(exchangePackage, exchange, contents))
                .collect(Collectors.toList());
    }

    private ExchangeReceiverHolderTemplateData(final String exchangePackage,
                                               final CodeGenerationParameter exchange,
                                               final List<Content> contents) {
        final List<ExchangeReceiversParameter> receiversParameters = ExchangeReceiversParameter.from(exchange);

        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, exchangePackage)
                        .and(AGGREGATE_PROTOCOL_NAME, exchange.parent().value)
                        .and(EXCHANGE_RECEIVERS, ExchangeReceiversParameter.from(exchange))
                        .addImports(resolveImports(exchange, receiversParameters, contents))
                        .andResolve(EXCHANGE_RECEIVER_HOLDER_NAME, params -> standard().resolveClassname(params));
    }

    private Set<String> resolveImports(final CodeGenerationParameter exchange,
                                       final List<ExchangeReceiversParameter> receiversParameters,
                                       final List<Content> contents) {
        final CodeGenerationParameter aggregate = exchange.parent();

        final List<TemplateStandard> standards =
                Stream.of(DATA_OBJECT, AGGREGATE_PROTOCOL).collect(Collectors.toList());

        if(receiversParameters.stream().anyMatch(receiver -> !receiver.isModelFactoryMethod())) {
            standards.add(TemplateStandard.AGGREGATE);
        }

        final Set<String> imports = standards.stream().map(standard -> {
                final String typeName = standard.resolveClassname(aggregate.value);
                return ContentQuery.findFullyQualifiedClassName(standard, typeName, contents);
            }).collect(Collectors.toSet());

        final Stream<CodeGenerationParameter> involvedStateFields =
                exchange.retrieveAllRelated(Label.RECEIVER).flatMap(receiver -> {
                            final String methodName = receiver.retrieveRelatedValue(MODEL_METHOD);
                            return AggregateDetail.findInvolvedStateFields(aggregate, methodName);
                        });

        final Set<String> valueObjects =
                ValueObjectDetail.retrieveQualifiedNames(contents, involvedStateFields);

        return Stream.of(imports, valueObjects).flatMap(Set::stream).collect(Collectors.toSet());
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
