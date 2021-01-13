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

import static java.util.stream.Collectors.toList;

public class ExchangeTemplateDataFactory {

    public static List<TemplateData> build(final String exchangePackage,
                                           final Stream<CodeGenerationParameter> aggregates,
                                           final List<Content> contents) {
        final List<TemplateData> mappers =
                ExchangeMapperTemplateData.from(exchangePackage, aggregates, contents);

        final List<TemplateData> holders =
                ExchangeReceiverHolderTemplateData.from(exchangePackage, aggregates, contents);

        final List<TemplateData> adapters =
                ExchangeAdapterTemplateData.from(exchangePackage, aggregates, contents);

        final List<TemplateData> properties =
                Arrays.asList(ExchangePropertiesTemplateData.from(aggregates));

        return Stream.of(mappers, holders, adapters, properties).flatMap(List::stream).collect(toList());
    }
}
