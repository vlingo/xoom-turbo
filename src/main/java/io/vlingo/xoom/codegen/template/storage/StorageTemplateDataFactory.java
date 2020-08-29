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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StorageTemplateDataFactory {

    private final static String PACKAGE_PATTERN = "%s.%s.%s";
    private final static String PARENT_PACKAGE_NAME = "infrastructure";
    private final static String PERSISTENCE_PACKAGE_NAME = "persistence";

    public static List<TemplateData> build(final String basePackage,
                                           final List<Content> contents,
                                           final StorageType storageType,
                                           final Map<Model, DatabaseType> databases,
                                           final ProjectionType projectionType,
                                           final Boolean internalGeneration,
                                           final Boolean useAnnotations,
                                           final Boolean useCQRS) {
        final String persistencePackage = resolvePackage(basePackage);

        final List<TemplateData> templatesData = new ArrayList<>();

        templatesData.addAll(AdapterTemplateData.from(persistencePackage, storageType, contents));

        templatesData.addAll(buildStoreProvidersTemplateData(basePackage, persistencePackage,
                useCQRS, useAnnotations, storageType, projectionType, templatesData,
                contents));

        if(!internalGeneration) {
            templatesData.add(new DatabasePropertiesTemplateData(databases));
        }

        return templatesData;
    }


    private static List<TemplateData> buildStoreProvidersTemplateData(final String basePackage,
                                                                      final String persistencePackage,
                                                                      final Boolean useCQRS,
                                                                      final Boolean useAnnotations,
                                                                      final StorageType storageType,
                                                                      final ProjectionType projectionType,
                                                                      final List<TemplateData> stateAdaptersTemplateData,
                                                                      final List<Content> contents) {
        if(useAnnotations) {
            final TemplateData annotatedTemplateData =
                    AnnotatedStorageProviderTemplateData.from(basePackage, persistencePackage,
                            useCQRS, storageType, projectionType, stateAdaptersTemplateData, contents);

            return Arrays.asList(annotatedTemplateData);
        }

        return StorageProviderTemplateData.from(persistencePackage, storageType, projectionType,
                stateAdaptersTemplateData, contents, Model.applicableFor(useCQRS));
    }


    private static String resolvePackage(final String basePackage) {
        if(basePackage.endsWith(".infrastructure")) {
            return basePackage + "." + PERSISTENCE_PACKAGE_NAME;
        }
        return String.format(PACKAGE_PATTERN, basePackage, PARENT_PACKAGE_NAME, PERSISTENCE_PACKAGE_NAME).toLowerCase();
    }

}
