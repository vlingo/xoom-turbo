// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;


import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.storage.Model;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.template.TemplateParameter.MODEL_CLASSIFICATION;
import static io.vlingo.xoom.codegen.template.TemplateParameter.STORAGE_TYPE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.PROJECTION_DISPATCHER_PROVIDER;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STORE_PROVIDER;


public class StoreProviderParameter {

    private final String className;
    private final String arguments;

    private static final String STORE_PROVIDER_ARGS_PATTERN = "stage, %s";
    private static final String STORE_PROVIDER_ARGS_WITH_PROJECTIONS_PATTERN = STORE_PROVIDER_ARGS_PATTERN + ", %s.using(stage).storeDispatcher";

    public static List<StoreProviderParameter> from(final StorageType storageType,
                                                    final Boolean useCQRS,
                                                    final Boolean useProjections) {
        if(!storageType.isEnabled()) {
            return Collections.emptyList();
        }

        return Model.applicableFor(useCQRS)
                .map(model -> new StoreProviderParameter(storageType, model, useProjections))
                .collect(Collectors.toList());
    }

    private StoreProviderParameter(final StorageType storageType,
                                   final Model model,
                                   final Boolean useProjections) {
        final TemplateParameters parameters =
                TemplateParameters.with(STORAGE_TYPE, storageType)
                        .and(MODEL_CLASSIFICATION, model);

        this.className = STORE_PROVIDER.resolveClassname(parameters);
        this.arguments = resolveArguments(model, storageType, useProjections);
    }

    private String resolveArguments(final Model model,
                                    final StorageType storageType,
                                    final Boolean useProjections) {
        final String typeRegistryObjectName =
                storageType.resolveTypeRegistryObjectName(model);

        final String dispatcherProviderClassName =
                PROJECTION_DISPATCHER_PROVIDER.resolveClassname();

        if(model.isQueryModel() || !useProjections) {
            return String.format(STORE_PROVIDER_ARGS_PATTERN, typeRegistryObjectName);
        }

        return String.format(STORE_PROVIDER_ARGS_WITH_PROJECTIONS_PATTERN, typeRegistryObjectName, dispatcherProviderClassName);
    }

    public String getClassName() {
        return className;
    }

    public String getArguments() {
        return arguments;
    }
}
