// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;


import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.storage.ModelClassification;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.ArrayList;
import java.util.List;

import static io.vlingo.xoom.codegen.template.TemplateStandard.PROJECTION_DISPATCHER_PROVIDER;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STORAGE_PROVIDER;
import static io.vlingo.xoom.codegen.template.storage.ModelClassification.*;


public class ProviderParameter {

    private final String initialization;
    private final String arguments;

    private static final String DISPATCHER_PROVIDER_ARG = "stage";
    private static final String STORE_PROVIDER_ARGS_PATTERN = "stage, %s";
    private static final String STORE_PROVIDER_ARGS_WITH_PROJECTIONS_PATTERN = STORE_PROVIDER_ARGS_PATTERN + ", projectionDispatcherProvider.storeDispatcher";
    private static final String DISPATCHER_PROVIDER_INITIALIZATION_PATTERN = "final %s projectionDispatcherProvider = %s";

    public static List<ProviderParameter> from(final StorageType storageType,
                                               final Boolean useCQRS,
                                               final Boolean useProjections) {
        final List<ProviderParameter> providers = new ArrayList<>();
        if(storageType.isEnabled()) {
            if (useProjections) {
                providers.add(new ProviderParameter(PROJECTION_DISPATCHER_PROVIDER));
            }
            if (!useCQRS) {
                providers.add(new ProviderParameter(STORAGE_PROVIDER, storageType, SINGLE, useProjections));
            } else {
                providers.add(0, new ProviderParameter(STORAGE_PROVIDER, storageType, QUERY, useProjections));
                providers.add(new ProviderParameter(STORAGE_PROVIDER, storageType, COMMAND, useProjections));
            }
        }
        return providers;
    }

    private ProviderParameter(final TemplateStandard standard) {
        if(!standard.isProjectionDispatcherProvider()) {
           throw new IllegalStateException("This constructor does not support CodeTemplateStandard");
        }
        final String className = PROJECTION_DISPATCHER_PROVIDER.resolveClassname(TemplateParameters.empty());
        this.initialization = String.format(DISPATCHER_PROVIDER_INITIALIZATION_PATTERN, className, className);
        this.arguments = DISPATCHER_PROVIDER_ARG;
    }

    private ProviderParameter(final TemplateStandard standard,
                              final StorageType storageType,
                              final ModelClassification modelClassification,
                              final Boolean useProjections) {
        if(standard.isProjectionDispatcherProvider()) {
            throw new IllegalStateException();
        }

        final String argumentsPattern =
                resolveArgumentsPattern(modelClassification, useProjections);

        final String typeRegistryObjectName =
                storageType.resolveTypeRegistryObjectName(modelClassification);

        final TemplateParameters parameters =
                TemplateParameters.with(TemplateParameter.STORAGE_TYPE, storageType)
                        .and(TemplateParameter.MODEL_CLASSIFICATION, modelClassification);

        this.initialization = STORAGE_PROVIDER.resolveClassname(parameters);
        this.arguments = String.format(argumentsPattern, typeRegistryObjectName);
    }

    private String resolveArgumentsPattern(final ModelClassification modelClassification, final Boolean useProjections) {
        if(modelClassification.isQueryModel() || !useProjections) {
            return STORE_PROVIDER_ARGS_PATTERN;
        }
        return STORE_PROVIDER_ARGS_WITH_PROJECTIONS_PATTERN;
    }

    public String getInitialization() {
        return initialization;
    }

    public String getArguments() {
        return arguments;
    }
}
