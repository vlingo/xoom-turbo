// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.content.ContentQuery.findFullyQualifiedClassNames;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
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
                                              final List<TemplateData> stateAdaptersTemplateData,
                                              final List<Content> contents,
                                              final Model model) {
        final List<AdapterParameter> adapterParameters =
                AdapterParameter.from(stateAdaptersTemplateData);

        final List<String> sourceClassQualifiedNames =
                storageType.requireAdapters(model) ?
                        findFullyQualifiedClassNames(storageType.adapterSourceClassStandard, contents) :
                        Collections.emptyList();

        return TemplateParameters.with(STORAGE_TYPE, storageType).and(PROJECTION_TYPE, projectionType)
                .and(MODEL, model).and(IMPORTS, ImportParameter.of(sourceClassQualifiedNames))
                .and(PACKAGE_NAME, packageName).and(USE_PROJECTIONS, projectionType.isProjectionEnabled())
                .and(ADAPTERS, adapterParameters).andResolve(STORAGE_PROVIDER_NAME, params -> STORE_PROVIDER.resolveClassname(params))
                .and(REQUIRE_ADAPTERS, storageType.requireAdapters(model));
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
