// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;

public class QueriesTemplateDataFactory {

    public static List<TemplateData> from(final String persistencePackage,
                                          final Boolean useCQRS,
                                          final List<Content> contents) {
        if(!useCQRS) {
            return Collections.emptyList();
        }
        return ContentQuery.findClassNames(AGGREGATE_PROTOCOL, contents)
                .stream().map(protocol -> createTemplates(protocol, persistencePackage, contents))
                .flatMap(templateData ->  templateData.stream()).collect(Collectors.toList());
    }

    private static List<TemplateData> createTemplates(final String protocol,
                                                      final String persistencePackage,
                                                      final List<Content> contents) {
        final TemplateParameters parameters =
                createParameters(persistencePackage, protocol, contents);

        return Arrays.asList(new QueriesTemplateData(protocol, parameters),
                new QueriesActorTemplateData(protocol, parameters));
    }

    private static TemplateParameters createParameters(final String persistencePackage,
                                                       final String aggregateProtocol,
                                                       final List<Content> contents) {
        final String queryByIdMethodName =
                buildQueryByIdMethodName(aggregateProtocol);

        final String queryAllMethodName =
                buildQueryAllMethodName(aggregateProtocol);

        final String dataObjectName =
                DATA_OBJECT.resolveClassname(aggregateProtocol);

        final String dataObjectQualifiedName =
                ContentQuery.findFullyQualifiedClassName(DATA_OBJECT, dataObjectName, contents);

        return TemplateParameters.with(PACKAGE_NAME, persistencePackage)
                .and(DATA_OBJECT_NAME, dataObjectName)
                .and(QUERY_ID_METHOD_NAME, queryByIdMethodName)
                .and(QUERY_ALL_METHOD_NAME, queryAllMethodName)
                .and(IMPORTS, ImportParameter.of(dataObjectQualifiedName));
    }

    private static String buildQueryByIdMethodName(final String aggregateProtocol) {
        return Introspector.decapitalize(aggregateProtocol) + "Of";
    }

    private static String buildQueryAllMethodName(final String aggregateProtocol) {
        final String formatted = Introspector.decapitalize(aggregateProtocol);
        return formatted.endsWith("s") ? formatted : formatted + "s";
    }

}