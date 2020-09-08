// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelTemplateDataFactory {

    private static final String EVENTS_SEPARATOR = ";";
    private static final String AGGREGATE_SEPARATOR = "\\|";
    private final static String PACKAGE_PATTERN = "%s.%s.%s";
    private final static String PARENT_PACKAGE_NAME = "model";

    public static List<TemplateData> build(final String basePackage,
                                           final String aggregatesData,
                                           final StorageType storageType) {
        return Stream.of(aggregatesData.split(AGGREGATE_SEPARATOR)).flatMap(aggregateData -> {
            final List<String> eventsNames = new ArrayList<>();
            final String[] splittedAggregatesData = aggregateData.split(EVENTS_SEPARATOR);
            final String protocol = splittedAggregatesData[0];
            final String packageName = resolvePackage(basePackage, protocol);

            if(splittedAggregatesData.length > 1) {
                Stream.of(splittedAggregatesData).skip(1).forEach(eventsNames::add);
            }

            return loadTemplates(packageName, protocol, storageType, eventsNames);
        }).collect(Collectors.toList());
    }

    private static Stream<TemplateData> loadTemplates(final String packageName,
                                                      final String aggregateProtocolName,
                                                      final StorageType storageType,
                                                      final List<String> eventsNames) {
        final List<TemplateData> templatesData = new ArrayList<>();
        templatesData.add(new AggregateProtocolTemplateData(packageName, aggregateProtocolName));
        templatesData.add(new AggregateTemplateData(packageName, aggregateProtocolName, storageType));
        templatesData.add(new StateTemplateData(packageName, aggregateProtocolName, storageType));
        templatesData.addAll(DomainEventTemplateData.from(aggregateProtocolName, packageName, eventsNames));
        return templatesData.stream();
    }

    private static String resolvePackage(final String basePackage, final String protocolName) {
        return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME, protocolName).toLowerCase();
    }

}
