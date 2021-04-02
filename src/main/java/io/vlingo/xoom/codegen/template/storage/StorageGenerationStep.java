// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;

import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.ADAPTER;

public class StorageGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final String basePackage = context.parameterOf(PACKAGE);
        final String appName = context.parameterOf(APPLICATION_NAME);
        final StorageType storageType = context.parameterOf(STORAGE_TYPE, StorageType::of);
        final ProjectionType projectionType = context.parameterOf(PROJECTION_TYPE, ProjectionType::valueOf);
        final Boolean useAnnotations = context.parameterOf(USE_ANNOTATIONS, Boolean::valueOf);
        final Boolean useCQRS  = context.parameterOf(CQRS, Boolean::valueOf);
        final List<TemplateData> templatesData =
                StorageTemplateDataFactory.build(basePackage, appName, context.contents(), storageType,
                        context.databases(), projectionType, context.isInternalGeneration(),
                        useAnnotations, useCQRS);

        return filterConditionally(useAnnotations, templatesData);
    }

    private List<TemplateData> filterConditionally(final Boolean useAnnotations, final List<TemplateData> templatesData) {
        if(!useAnnotations) {
            return templatesData;
        }
        return templatesData.stream().filter(data -> !data.hasStandard(ADAPTER)).collect(Collectors.toList());
    }

    @Override
    public boolean shouldProcess(final CodeGenerationContext context) {
        return context.parameterOf(STORAGE_TYPE, StorageType::of).isEnabled();
    }
}
