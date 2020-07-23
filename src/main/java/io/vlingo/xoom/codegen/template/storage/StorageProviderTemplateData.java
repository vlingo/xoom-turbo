// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.file.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.content.ContentQuery.findFullyQualifiedClassNames;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STORAGE_PROVIDER;

public class StorageProviderTemplateData extends TemplateData {

    private final TemplateParameters templateParameters;

    public static List<TemplateData> from(final String persistencePackage,
                                          final StorageType storageType,
                                          final ProjectionType projectionType,
                                          final Map<ModelClassification, DatabaseType> databases,
                                          final List<TemplateData> stateAdaptersTemplateData,
                                          final List<Content> contents) {
        return Stream.of(ModelClassification.values())
                .filter(modelClassification -> databases.containsKey(modelClassification))
                .map(modelClassification -> new StorageProviderTemplateData(persistencePackage,
                        storageType, databases.get(modelClassification), projectionType,
                        modelClassification, stateAdaptersTemplateData, contents))
                .collect(Collectors.toList());
    }

    private StorageProviderTemplateData(final String persistencePackage,
                                        final StorageType storageType,
                                        final DatabaseType databaseType,
                                        final ProjectionType projectionType,
                                        final ModelClassification modelClassification,
                                        final List<TemplateData> stateAdaptersTemplateData,
                                        final List<Content> contents) {
        this.templateParameters =
                loadParameters(persistencePackage, storageType, databaseType, projectionType,
                        modelClassification, stateAdaptersTemplateData, contents);
    }

    private TemplateParameters loadParameters(final String packageName,
                                              final StorageType storageType,
                                              final DatabaseType databaseType,
                                              final ProjectionType projectionType,
                                              final ModelClassification modelClassification,
                                              final List<TemplateData> stateAdaptersTemplateData,
                                              final List<Content> contents) {
        final String storageClassName =
                storageType.actorFor(databaseType);

        final List<AdapterParameter> adapterParameters =
                AdapterParameter.from(stateAdaptersTemplateData);

        final List<String> sourceClassQualifiedNames =
                storageType.requireAdapters(modelClassification) ?
                        findFullyQualifiedClassNames(storageType.adapterSourceClassStandard, contents) :
                        Collections.emptyList();

        return TemplateParameters.with(STORAGE_TYPE, storageType)
                .and(MODEL_CLASSIFICATION, modelClassification).and(DATABASE_TYPE, databaseType)
                .and(IMPORTS, ImportParameter.of(sourceClassQualifiedNames)).and(STORE_NAME, storageClassName)
                .and(PACKAGE_NAME, packageName).and(USE_PROJECTIONS, projectionType.isProjectionEnabled())
                .and(ADAPTERS, adapterParameters).and(CONNECTION_URL, databaseType.connectionUrl)
                .and(CONFIGURABLE, databaseType.configurable).and(PROJECTION_TYPE, projectionType)
                .andResolve(STORAGE_PROVIDER_NAME, params -> STORAGE_PROVIDER.resolveClassname(params))
                .enrich(params -> databaseType.addConfigurationParameters(params))
                .and(REQUIRE_ADAPTERS, storageType.requireAdapters(modelClassification));
    }

    @Override
    public TemplateParameters parameters() {
        return templateParameters;
    }

    @Override
    public TemplateStandard standard() {
        return STORAGE_PROVIDER;
    }

}
