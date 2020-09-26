// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.template.TemplateData;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class AutoDispatchMappingTemplateDataFactory {

    private static final String REST_RESOURCES_SEPARATOR = ";";

    public static List<TemplateData> build(final String basePackage,
                                           final String restResourcesData,
                                           final Boolean useCQRS,
                                           final List<TemplateData> queriesData,
                                           final List<Content> contents) {
        final Function<String, Stream<TemplateData>> mapper = aggregateName ->
                Stream.of(new AutoDispatchMappingTemplateData(basePackage,
                                aggregateName, useCQRS, contents),
                        new AutoDispatchHandlersMappingTemplateData(basePackage, aggregateName,
                                useCQRS, queriesData, contents));

        return Stream.of(restResourcesData.split(REST_RESOURCES_SEPARATOR)).flatMap(mapper).collect(toList());
    }
}
