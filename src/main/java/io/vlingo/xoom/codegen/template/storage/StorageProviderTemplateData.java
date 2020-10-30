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

public class StorageProviderTemplateData extends TemplateData {

    private final TemplateParameters templateParameters;

    public static List<TemplateData> from(final String persistencePackage,
                                          final StorageType storageType,
                                          final ProjectionType projectionType,
                                          final List<TemplateData> stateAdaptersTemplateData,
                                          final List<Content> contents,
                                          final Stream<Model> models) {
        return models.sorted().map(model -> new StorageProviderTemplateData(persistencePackage, storageType,
                projectionType, stateAdaptersTemplateData, contents, model)).collect(Collectors.toList());
    }

    private StorageProviderTemplateData(final String persistencePackage,
                                        final StorageType storageType,
                                        final ProjectionType projectionType,
                                        final List<TemplateData> stateAdaptersTemplateData,
                                        final List<Content> contents,
                                        final Model model) {
        this.templateParameters =
                loadParameters(persistencePackage, storageType, projectionType,
                        stateAdaptersTemplateData, contents, model);
    }

    private TemplateParameters loadParameters(final String packageName,
                                              final StorageType storageType,
                                              final ProjectionType projectionType,
                                              final List<TemplateData> templatesData,
                                              final List<Content> contents,
                                              final Model model) {
        final List<AdapterParameter> adapterParameters = AdapterParameter.from(templatesData);
        final List<QueriesParameter> queriesParameters = QueriesParameter.from(model, contents, templatesData);
        final Set<ImportParameter> importParameters = resolveImports(model, storageType, contents, queriesParameters);

        return TemplateParameters.with(STORAGE_TYPE, storageType).and(PROJECTION_TYPE, projectionType)
                .and(MODEL, model).and(IMPORTS, importParameters).and(PACKAGE_NAME, packageName)
                .and(REQUIRE_ADAPTERS, storageType.requireAdapters(model))
                .and(USE_PROJECTIONS, projectionType.isProjectionEnabled())
                .and(ADAPTERS, adapterParameters).and(QUERIES, queriesParameters)
                .and(AGGREGATES, ContentQuery.findClassNames(AGGREGATE, contents))
                .andResolve(STORE_PROVIDER_NAME, params -> STORE_PROVIDER.resolveClassname(params));
    }

    private Set<ImportParameter> resolveImports(final Model model,
                                                final StorageType storageType,
                                                final List<Content> contents,
                                                final List<QueriesParameter> queriesParameters) {
        final Set<String> sourceClassQualifiedNames =
                storageType.resolveAdaptersQualifiedName(model, contents);

        final Set<String> queriesQualifiedNames = queriesParameters.stream()
                        .flatMap(param -> param.getQualifiedNames().stream())
                        .collect(Collectors.toSet());

        final Set<String> aggregateActorQualifiedNames = storageType.isSourced() ?
                ContentQuery.findFullyQualifiedClassNames(AGGREGATE, contents) : new HashSet<>();

        return ImportParameter.of(sourceClassQualifiedNames, queriesQualifiedNames, aggregateActorQualifiedNames);
    }

    @Override
    public TemplateParameters parameters() {
        return templateParameters;
    }

    @Override
    public TemplateStandard standard() {
        return STORE_PROVIDER;
    }

}
