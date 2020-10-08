// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.bootstrap;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.ImportParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static io.vlingo.xoom.codegen.parameter.Label.APPLICATION_NAME;
import static io.vlingo.xoom.codegen.parameter.Label.PROJECTION_TYPE;
import static io.vlingo.xoom.codegen.parameter.Label.STORAGE_TYPE;
import static io.vlingo.xoom.codegen.parameter.Label.USE_ANNOTATIONS;
import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public abstract class BootstrapTemplateData extends TemplateData {

    private final static String PACKAGE_PATTERN = "%s.%s";
    private final static String INFRA_PACKAGE_NAME = "infrastructure";

    private final TemplateParameters parameters;
    private final static List<BootstrapTemplateData> TEMPLATES =
            Arrays.asList(new XoomInitializerTemplateData(), new AnnotatedBootstrapTemplateData(),
                    new DefaultBootstrapTemplateData());

    protected BootstrapTemplateData() {
        this.parameters = TemplateParameters.empty();
    }

    public static TemplateData from(final CodeGenerationContext context) {
        final Predicate<BootstrapTemplateData> supportCondition =
                templateData -> templateData.support(context);

        final BootstrapTemplateData bootstrapTemplateData  =
                TEMPLATES.stream().filter(supportCondition).findFirst().get();

        bootstrapTemplateData.handleParameters(context);

        return bootstrapTemplateData;
    }

    private void handleParameters(final CodeGenerationContext context) {
        loadParameters(context);
        enrichParameters(context);
    }

    private TemplateParameters loadParameters(final CodeGenerationContext context) {
        final Boolean useCQRS = context.parameterOf(CQRS, Boolean::valueOf);
        final String packageName = resolvePackage(context.parameterOf(PACKAGE));
        final Boolean useAnnotations = context.parameterOf(USE_ANNOTATIONS, Boolean::valueOf);
        final StorageType storageType = context.parameterOf(STORAGE_TYPE, StorageType::valueOf);
        final ProjectionType projectionType = context.parameterOf(PROJECTION_TYPE, ProjectionType::valueOf);

        final Set<ImportParameter> imports =
                loadImports(storageType, context.contents(), useCQRS, useAnnotations);

        final List<TypeRegistryParameter> typeRegistryParameters =
                TypeRegistryParameter.from(storageType, useCQRS);

        final List<StoreProviderParameter> storeProviderParameters =
                StoreProviderParameter.from(storageType, useCQRS, projectionType.isProjectionEnabled());

        return this.parameters.and(IMPORTS, imports)
                .and(PACKAGE_NAME, packageName)
                .and(PROVIDERS, storeProviderParameters)
                .and(TYPE_REGISTRIES, typeRegistryParameters)
                .and(USE_PROJECTIONS, projectionType.isProjectionEnabled())
                .and(TemplateParameter.APPLICATION_NAME, context.parameterOf(APPLICATION_NAME))
                .and(TemplateParameter.USE_ANNOTATIONS, context.parameterOf(USE_ANNOTATIONS, Boolean::valueOf))
                .andResolve(PROJECTION_DISPATCHER_PROVIDER_NAME,
                        param -> PROJECTION_DISPATCHER_PROVIDER.resolveClassname(param));
    }

    protected abstract void enrichParameters(final CodeGenerationContext context);

    protected abstract boolean support(final CodeGenerationContext context);

    private Set<ImportParameter> loadImports(final StorageType storageType,
                                             final List<Content> contents,
                                             final Boolean useCQRS,
                                             final Boolean useAnnotations) {
        if(useAnnotations) {
            return new HashSet<>();
        }

        final Set<String> otherFullyQualifiedNames =
                ContentQuery.findFullyQualifiedClassNames(contents,
                        STORE_PROVIDER, PROJECTION_DISPATCHER_PROVIDER);

        final Set<String> typeRegistriesFullyQualifiedNames =
                storageType.resolveTypeRegistryQualifiedNames(useCQRS);

        return ImportParameter.of(otherFullyQualifiedNames, typeRegistriesFullyQualifiedNames);
    }

    protected String resolvePackage(final String basePackage) {
        return String.format(PACKAGE_PATTERN, basePackage, INFRA_PACKAGE_NAME).toLowerCase();
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return BOOTSTRAP;
    }

}
