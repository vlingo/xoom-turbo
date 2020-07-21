// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.Content;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import io.vlingo.xoom.codegen.template.TemplateData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StorageTemplateDataFactory {

    private final static String PACKAGE_PATTERN = "%s.%s.%s";
    private final static String PARENT_PACKAGE_NAME = "infrastructure";
    private final static String PERSISTENCE_PACKAGE_NAME = "persistence";

    public static List<TemplateData> build(final String basePackage,
                                           final List<Content> contents,
                                           final StorageType storageType,
                                           final Map<ModelClassification, DatabaseType> databases,
                                           final ProjectionType projectionType) {
        final String persistencePackage =
                resolvePackage(basePackage, PARENT_PACKAGE_NAME, PERSISTENCE_PACKAGE_NAME);

        final List<TemplateData> stateAdaptersTemplateData =
                AdapterTemplateData.from(persistencePackage, storageType, contents);

        final List<TemplateData> storeProvidersTemplateData =
                StorageProviderTemplateData.from(persistencePackage, storageType, projectionType,
                        databases, stateAdaptersTemplateData, contents);

        return Stream.of(stateAdaptersTemplateData, storeProvidersTemplateData)
                .flatMap(templatesData -> templatesData.stream())
                .collect(Collectors.toList());
    }

    private static String resolvePackage(final String... additionalPackages) {
        return String.format(PACKAGE_PATTERN, additionalPackages).toLowerCase();
    }

}
