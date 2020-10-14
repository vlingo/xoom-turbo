// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.autodispatch;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.vlingo.http.Method.GET;
import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.parameter.Label.READ_ONLY;
import static java.util.stream.Collectors.toList;

public class AutoDispatchMappingTemplateDataFactory {

    private final static CodeGenerationParameter DEFAULT_QUERY_ROUTE_PARAMETER =
            CodeGenerationParameter.of(ROUTE_SIGNATURE, "queryAll")
                    .relate(ROUTE_METHOD, GET).relate(READ_ONLY, "true");

    public static List<TemplateData> build(final CodeGenerationParameters parameters,
                                           final List<TemplateData> queriesData,
                                           final List<Content> contents) {
        final String basePackage = parameters.retrieveValue(Label.PACKAGE);
        final boolean useCQRS = Boolean.valueOf(parameters.retrieveValue(Label.CQRS));

        final Function<CodeGenerationParameter, Stream<TemplateData>> mapper = aggregate ->
                Stream.of(new AutoDispatchMappingTemplateData(basePackage,
                                aggregate, useCQRS, contents),
                        new AutoDispatchHandlersMappingTemplateData(basePackage, aggregate,
                                queriesData, contents, useCQRS));

        return parameters.retrieveAll(AGGREGATE).flatMap(mapper).collect(toList());
    }
}
