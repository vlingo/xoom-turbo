// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.file.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;

import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STATE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STORE_PROVIDER;

public class AnnotatedStorageProviderTemplateData extends TemplateData {

    private final TemplateParameters templateParameters;

    public static TemplateData from(final String basePackage,
                                    final String persistencePackage,
                                    final Boolean useCQRS,
                                    final StorageType storageType,
                                    final ProjectionType projectionType,
                                    final List<TemplateData> stateAdaptersTemplateData,
                                    final List<Content> contents) {
        return new AnnotatedStorageProviderTemplateData(basePackage, persistencePackage, useCQRS,
                storageType, projectionType, stateAdaptersTemplateData, contents);
    }

    private AnnotatedStorageProviderTemplateData(final String basePackage,
                                                 final String persistencePackage,
                                                 final Boolean useCQRS,
                                                 final StorageType storageType,
                                                 final ProjectionType projectionType,
                                                 final List<TemplateData> stateAdaptersTemplateData,
                                                 final List<Content> contents){
        this.templateParameters =
                loadParameters(basePackage, persistencePackage,useCQRS, storageType,
                        projectionType, stateAdaptersTemplateData, contents);
    }

    private TemplateParameters loadParameters(final String basePackage,
                                              final String persistencePackage,
                                              final Boolean useCQRS,
                                              final StorageType storageType,
                                              final ProjectionType projectionType,
                                              final List<TemplateData> stateAdaptersTemplateData,
                                              final List<Content> contents)  {
        return TemplateParameters.with(BASE_PACKAGE, basePackage)
                .and(IMPORTS, resolveImports(contents))
                .and(PACKAGE_NAME, persistencePackage)
                .and(STORAGE_TYPE, storageType.name())
                .and(REQUIRE_ADAPTERS, !stateAdaptersTemplateData.isEmpty())
                .and(USE_PROJECTIONS, projectionType.isProjectionEnabled())
                .and(ADAPTERS, AdapterParameter.from(stateAdaptersTemplateData))
                .and(PROJECTIONS, ProjectionParameter.from(contents))
                .and(USE_CQRS, useCQRS).and(USE_ANNOTATIONS, true)
                .andResolve(STORAGE_PROVIDER_NAME, params -> STORE_PROVIDER.resolveClassname(params));

    }

    private List<ImportParameter> resolveImports(final List<Content> contents) {
        return ImportParameter.of(ContentQuery.findFullyQualifiedClassNames(STATE, contents));
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
