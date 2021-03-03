// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateProtocolTemplateData;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateStateTemplateData;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateTemplateData;
import io.vlingo.xoom.codegen.template.model.domainevent.DomainEventTemplateData;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;

public class ModelTemplateDataFactory {

    private final static String PACKAGE_PATTERN = "%s.%s.%s";
    private final static String PARENT_PACKAGE_NAME = "model";

    public static List<TemplateData> from(final CodeGenerationParameters parameters) {
        final String basePackage = parameters.retrieveValue(PACKAGE);
        final Language language = parameters.retrieveValue(LANGUAGE, Language::valueOf);
        final StorageType storageType = StorageType.of(parameters.retrieveValue(STORAGE_TYPE));
        return parameters.retrieveAll(AGGREGATE).flatMap(aggregate -> {
            final String packageName = AggregateDetail.resolvePackage(basePackage, aggregate.value);
            return loadTemplates(packageName, language, aggregate, storageType);
        }).collect(Collectors.toList());
    }

    private static Stream<TemplateData> loadTemplates(final String packageName,
                                                      final Language language,
                                                      final CodeGenerationParameter aggregateParameter,
                                                      final StorageType storageType) {
        final List<TemplateData> templatesData = new ArrayList<>();
        templatesData.add(new AggregateProtocolTemplateData(packageName, aggregateParameter));
        templatesData.add(new AggregateTemplateData(packageName, aggregateParameter, storageType));
        templatesData.add(new AggregateStateTemplateData(packageName, language, aggregateParameter, storageType));
        templatesData.addAll(DomainEventTemplateData.from(packageName, language, aggregateParameter));
        return templatesData.stream();
    }



}
