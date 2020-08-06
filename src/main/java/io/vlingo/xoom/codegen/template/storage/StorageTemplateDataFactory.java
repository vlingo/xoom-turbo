// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;

import java.util.Arrays;
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
                                           final Map<Model, DatabaseType> databases,
                                           final ProjectionType projectionType,
                                           final Boolean useAnnotations,
                                           final Boolean useCQRS) {
        final String persistencePackage = resolvePackage(basePackage);

        final List<TemplateData> stateAdaptersTemplateData =
                AdapterTemplateData.from(persistencePackage, storageType, contents);

        final List<TemplateData> storeProvidersTemplateData =
                buildStoreProvidersTemplateData(basePackage, persistencePackage, useCQRS,
                        useAnnotations, storageType, projectionType, databases,
                        stateAdaptersTemplateData, contents);

        return Stream.of(stateAdaptersTemplateData, storeProvidersTemplateData)
                .flatMap(templatesData -> templatesData.stream())
                .collect(Collectors.toList());
    }

    private static List<TemplateData> buildStoreProvidersTemplateData(final String basePackage,
                                                                      final String persistencePackage,
                                                                      final Boolean useCQRS,
                                                                      final Boolean useAnnotations,
                                                                      final StorageType storageType,
                                                                      final ProjectionType projectionType,
                                                                      final Map<Model, DatabaseType> databases,
                                                                      final List<TemplateData> stateAdaptersTemplateData,
                                                                      final List<Content> contents) {
        if(useAnnotations) {
            final TemplateData annotatedTemplateData =
                    AnnotatedStorageProviderTemplateData.from(basePackage, persistencePackage,
                            useCQRS, storageType, projectionType, stateAdaptersTemplateData, contents);

            return Arrays.asList(annotatedTemplateData);
        }

        return StorageProviderTemplateData.from(persistencePackage, storageType, projectionType,
                databases, stateAdaptersTemplateData, contents);
    }


    private static String resolvePackage(final String basePackage) {
        if(basePackage.endsWith(".infrastructure")) {
            return basePackage + "." + PERSISTENCE_PACKAGE_NAME;
        }
        return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME, PERSISTENCE_PACKAGE_NAME).toLowerCase();
    }

}
