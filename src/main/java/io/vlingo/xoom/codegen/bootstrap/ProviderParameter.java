// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.bootstrap;


import io.vlingo.xoom.codegen.CodeTemplateParameter;
import io.vlingo.xoom.codegen.CodeTemplateParameters;
import io.vlingo.xoom.codegen.CodeTemplateStandard;
import io.vlingo.xoom.codegen.storage.ModelClassification;
import io.vlingo.xoom.codegen.storage.StorageType;

import java.util.ArrayList;
import java.util.List;

import static io.vlingo.xoom.codegen.CodeTemplateStandard.PROJECTION_DISPATCHER_PROVIDER;
import static io.vlingo.xoom.codegen.CodeTemplateStandard.STORE_PROVIDER;
import static io.vlingo.xoom.codegen.storage.ModelClassification.*;


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

        if(useProjections) {
            providers.add(new ProviderParameter(PROJECTION_DISPATCHER_PROVIDER));
        }

        if(!useCQRS) {
            providers.add(new ProviderParameter(STORE_PROVIDER, storageType, SINGLE, useProjections));
        } else {
            providers.add(0, new ProviderParameter(STORE_PROVIDER, storageType, QUERY, useProjections));
            providers.add(new ProviderParameter(STORE_PROVIDER, storageType, COMMAND, useProjections));
        }

        return providers;
    }

    private ProviderParameter(final CodeTemplateStandard standard) {
        if(!standard.isProjectionDispatcherProvider()) {
           throw new IllegalStateException("This constructor does not support CodeTemplateStandard");
        }
        final String className = PROJECTION_DISPATCHER_PROVIDER.resolveClassname(CodeTemplateParameters.empty());
        this.initialization = String.format(DISPATCHER_PROVIDER_INITIALIZATION_PATTERN, className, className);
        this.arguments = DISPATCHER_PROVIDER_ARG;
    }

    private ProviderParameter(final CodeTemplateStandard standard,
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

        final CodeTemplateParameters parameters =
                CodeTemplateParameters.with(CodeTemplateParameter.STORAGE_TYPE, storageType)
                        .and(CodeTemplateParameter.MODEL_CLASSIFICATION, modelClassification);

        this.initialization = STORE_PROVIDER.resolveClassname(parameters);
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
