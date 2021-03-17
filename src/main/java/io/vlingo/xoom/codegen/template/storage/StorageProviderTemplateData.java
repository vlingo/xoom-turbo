// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.content.ClassFormatter;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STORE_PROVIDER;
import static java.util.stream.Collectors.toSet;

public class StorageProviderTemplateData extends TemplateData {

    private final TemplateParameters templateParameters;

    public static List<TemplateData> from(final String persistencePackage,
                                          final StorageType storageType,
                                          final ProjectionType projectionType,
                                          final List<TemplateData> stateAdaptersTemplateData,
                                          final List<Content> contents,
                                          final Stream<Model> models,
                                          final boolean useAnnotation) {
        return models.sorted().filter(model -> supportModel(model, useAnnotation))
                .map(model -> new StorageProviderTemplateData(persistencePackage, storageType,
                projectionType, stateAdaptersTemplateData, contents, useAnnotation, model))
                .collect(Collectors.toList());
    }

    protected StorageProviderTemplateData(final String persistencePackage,
                                          final StorageType storageType,
                                          final ProjectionType projectionType,
                                          final List<TemplateData> stateAdaptersTemplateData,
                                          final List<Content> contents,
                                          final boolean useAnnotation,
                                          final Model model) {
        this.templateParameters =
                loadParameters(persistencePackage, storageType, projectionType,
                        stateAdaptersTemplateData, contents, useAnnotation, model);
    }

    private TemplateParameters loadParameters(final String packageName,
                                              final StorageType storageType,
                                              final ProjectionType projectionType,
                                              final List<TemplateData> templatesData,
                                              final List<Content> contents,
                                              final boolean useAnnotation,
                                              final Model model) {
        final List<AdapterParameter> adapterParameters = AdapterParameter.from(templatesData);
        final List<QueriesParameter> queriesParameters = QueriesParameter.from(model, contents, templatesData);
        final Stream<String> persistentTypes = storageType.findPersistentQualifiedTypes(model, contents).stream();
        final Set<ImportParameter> importParameters = resolveImports(model, storageType, contents, queriesParameters);

        return TemplateParameters.with(STORAGE_TYPE, storageType).and(PROJECTION_TYPE, projectionType)
                .and(MODEL, model).and(IMPORTS, importParameters).and(PACKAGE_NAME, packageName)
                .and(REQUIRE_ADAPTERS, storageType.requireAdapters(model))
                .and(USE_PROJECTIONS, projectionType.isProjectionEnabled())
                .and(ADAPTERS, adapterParameters).and(QUERIES, queriesParameters)
                .and(AGGREGATES, ContentQuery.findClassNames(AGGREGATE, contents))
                .and(PERSISTENT_TYPES, persistentTypes.map(ClassFormatter::simpleNameOf).collect(toSet()))
                .andResolve(STORE_PROVIDER_NAME, params -> STORE_PROVIDER.resolveClassname(params))
                .and(USE_ANNOTATIONS, useAnnotation);
    }

    @SuppressWarnings("unchecked")
    private Set<ImportParameter> resolveImports(final Model model,
                                                final StorageType storageType,
                                                final List<Content> contents,
                                                final List<QueriesParameter> queriesParameters) {
        final Set<String> sourceClassQualifiedNames =
                storageType.resolveAdaptersQualifiedName(model, contents);

        final Set<String> persistentTypes =
                storageType.findPersistentQualifiedTypes(model, contents);

        final Set<String> queriesQualifiedNames = queriesParameters.stream()
                        .flatMap(param -> param.getQualifiedNames().stream())
                        .collect(toSet());

        final Set<String> aggregateActorQualifiedNames = storageType.isSourced() ?
                ContentQuery.findFullyQualifiedClassNames(AGGREGATE, contents) : new HashSet<>();

        return ImportParameter.of(sourceClassQualifiedNames, queriesQualifiedNames,
                aggregateActorQualifiedNames, persistentTypes);
    }

    private static boolean supportModel(final Model model, final boolean useAnnotation) {
        if(useAnnotation) {
            return model.isQueryModel();
        }
        return true;
    }

    @Override
    public TemplateParameters parameters() {
        return templateParameters;
    }

    @Override
    public TemplateStandard standard() {
        return STORE_PROVIDER;
    }

    @Override
    public boolean isPlaceholder() {
        return templateParameters.find(USE_ANNOTATIONS);
    }

}
