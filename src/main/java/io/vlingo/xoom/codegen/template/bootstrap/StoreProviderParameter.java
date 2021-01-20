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
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.MODEL;
import static io.vlingo.xoom.codegen.template.TemplateParameter.STORAGE_TYPE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


public class StoreProviderParameter {

    private final String className;
    private final String arguments;

    public static List<StoreProviderParameter> from(final StorageType storageType,
                                                    final Boolean useCQRS,
                                                    final Boolean useProjections,
                                                    final Boolean internalGeneration,
                                                    final Boolean hasProducerExchange) {
        if(!storageType.isEnabled()) {
            return Collections.emptyList();
        }

        return Model.applicableTo(useCQRS)
                .map(model -> new StoreProviderParameter(storageType, model, useProjections, internalGeneration, hasProducerExchange))
                .collect(toList());
    }

    private StoreProviderParameter(final StorageType storageType,
                                   final Model model,
                                   final Boolean useProjections,
                                   final Boolean internalGeneration,
                                   final Boolean hasProducerExchange) {
        final TemplateParameters parameters =
                TemplateParameters.with(STORAGE_TYPE, storageType)
                        .and(MODEL, model);

        this.className = STORE_PROVIDER.resolveClassname(parameters);
        this.arguments = resolveArguments(model, storageType, useProjections,
                        internalGeneration, hasProducerExchange);
    }

    private String resolveArguments(final Model model,
                                    final StorageType storageType,
                                    final Boolean useProjections,
                                    final Boolean internalGeneration,
                                    final Boolean hasProducerExchange) {
        final String typeRegistryObjectName =
                storageType.resolveTypeRegistryObjectName(model);

        final String exchangeDispatcherAccess =
                resolveExchangeDispatcherAccess(internalGeneration, hasProducerExchange);

        final String projectionDispatcher =
                PROJECTION_DISPATCHER_PROVIDER.resolveClassname() + ".using(stage).storeDispatcher";

        final List<String> arguments =
                Stream.of("stage", typeRegistryObjectName).collect(toList());

        if(!model.isQueryModel()) {
            if(useProjections) {
                arguments.add(projectionDispatcher);
            }
            arguments.add(exchangeDispatcherAccess);
        }

        return arguments.stream().filter(arg -> !arg.isEmpty()).collect(joining(", "));
    }

    private String resolveExchangeDispatcherAccess(final Boolean internalGeneration,
                                                   final Boolean hasProducerExchange) {
        if(internalGeneration) {
            return "initializer.exchangeDispatcher(stage)";
        }
        if(hasProducerExchange) {
            return EXCHANGE_BOOTSTRAP.resolveClassname() + ".init(stage).dispatcher()";
        }
        return "";
    }

    public String getClassName() {
        return className;
    }

    public String getArguments() {
        return arguments;
    }
}
