// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.EXCHANGE;
import static java.util.stream.Collectors.toList;

public class ExchangeTemplateDataFactory {

    public static List<TemplateData> build(final String exchangePackage,
                                           final Stream<CodeGenerationParameter> aggregates,
                                           final List<Content> contents) {
        final Stream<CodeGenerationParameter> filteredAggregates =
                aggregates.filter(aggregate -> aggregate.hasAny(EXCHANGE));

        final List<TemplateData> mappers =
                ExchangeMapperTemplateData.from(exchangePackage, filteredAggregates, contents);

        final List<TemplateData> holders =
                ExchangeReceiverHolderTemplateData.from(exchangePackage, filteredAggregates, contents);

        final List<TemplateData> adapters =
                ExchangeAdapterTemplateData.from(exchangePackage, filteredAggregates, contents);

        final List<TemplateData> properties =
                Arrays.asList(ExchangePropertiesTemplateData.from(filteredAggregates));

        final List<TemplateData> dispatcher =
                Arrays.asList(ExchangeDispatcherTemplateData.from(exchangePackage, filteredAggregates, contents));

        final List<TemplateData> bootstrap =
                Arrays.asList(ExchangeBootstrapTemplateData.from(exchangePackage, filteredAggregates, contents));

        return Stream.of(mappers, holders, adapters, properties, dispatcher, bootstrap).flatMap(List::stream).collect(toList());
    }
}
