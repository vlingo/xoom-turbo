// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.file.ImportParameter;
import io.vlingo.xoom.codegen.template.storage.StorageType;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public abstract class BootstrapTemplateData extends TemplateData {

    private final static String PACKAGE_PATTERN = "%s.%s";
    private final static String INFRA_PACKAGE_NAME = "infrastructure";

    private final TemplateParameters parameters;

    protected BootstrapTemplateData(final String basePackage,
                                    final String artifactId,
                                    final StorageType storageType,
                                    final Boolean useCQRS,
                                    final Boolean useProjections,
                                    final Boolean useAnnotations,
                                    final List<Content> contents) {
        this.parameters =
                loadParameters(basePackage, storageType, artifactId,
                        useCQRS, useProjections, useAnnotations, contents);

        enrichParameters(contents);
    }

    public static TemplateData from(final String basePackage,
                                    final String artifactId,
                                    final StorageType storageType,
                                    final Boolean useCQRS,
                                    final Boolean useProjections,
                                    final Boolean useAnnotations,
                                    final List<Content> contents) {
        if(useAnnotations) {
            return new AnnotatedBootstrapTemplateData(basePackage, artifactId,
                    storageType, useCQRS, useProjections, useAnnotations, contents);
        }

        return new DefaultBootstrapTemplateData(basePackage, artifactId,
                storageType, useCQRS, useProjections, useAnnotations, contents);
    }


    private TemplateParameters loadParameters(final String basePackage,
                                              final StorageType storageType,
                                              final String artifactId,
                                              final Boolean useCQRS,
                                              final Boolean useProjections,
                                              final Boolean useAnnotations,
                                              final List<Content> contents) {
        final String packageName = resolvePackage(basePackage);

        final List<ImportParameter> imports =
                loadImports(storageType, contents, useCQRS);

        final List<TypeRegistryParameter> typeRegistryParameters =
                TypeRegistryParameter.from(storageType, useCQRS);

        final List<ProviderParameter> providerParameters =
                ProviderParameter.from(storageType, useCQRS, useProjections);

        return TemplateParameters.with(IMPORTS, imports)
                .and(PACKAGE_NAME, packageName)
                .and(APPLICATION_NAME, artifactId)
                .and(PROVIDERS, providerParameters)
                .and(USE_PROJECTIONS, useProjections)
                .and(USE_ANNOTATIONS, useAnnotations)
                .and(TYPE_REGISTRIES, typeRegistryParameters)
                .andResolve(PROJECTION_DISPATCHER_PROVIDER_NAME,
                        param -> PROJECTION_DISPATCHER_PROVIDER.resolveClassname(param));
    }

    private List<ImportParameter> loadImports(final StorageType storageType,
                                              final List<Content> contents,
                                              final Boolean useCQRS) {
        final List<String> otherFullyQualifiedNames =
                ContentQuery.findFullyQualifiedClassNames(contents,
                        STORAGE_PROVIDER, PROJECTION_DISPATCHER_PROVIDER);

        final List<String> typeRegistriesFullyQualifiedNames =
                storageType.resolveTypeRegistryQualifiedNames(useCQRS);

        return ImportParameter.of(otherFullyQualifiedNames, typeRegistriesFullyQualifiedNames);
    }

    private String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, INFRA_PACKAGE_NAME).toLowerCase();
    }

    protected abstract void enrichParameters(final List<Content> contents);

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return BOOTSTRAP;
    }



}
