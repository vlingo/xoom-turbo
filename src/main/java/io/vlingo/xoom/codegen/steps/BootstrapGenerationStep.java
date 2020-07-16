// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.steps;


import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.ProjectionType;
import io.vlingo.xoom.codegen.TemplateData;
import io.vlingo.xoom.codegen.bootstrap.BootstrapTemplateData;
import io.vlingo.xoom.codegen.storage.StorageType;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;

public class BootstrapGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final String projectPath = context.projectPath();
        final String basePackage = context.propertyOf(PACKAGE);
        final String artifactId = context.propertyOf(APPLICATION_NAME);
        final Boolean useCQRS = context.propertyOf(CQRS, Boolean::valueOf);
        final Boolean useAnnotations = context.propertyOf(ANNOTATIONS, Boolean::valueOf);
        final StorageType storageType = context.propertyOf(STORAGE_TYPE, StorageType::valueOf);
        final ProjectionType projectionType = context.propertyOf(PROJECTIONS, ProjectionType::valueOf);

        final TemplateData templateData =
                BootstrapTemplateData.from(basePackage, projectPath, artifactId,
                        storageType, useCQRS, projectionType.isProjectionEnabled(),
                        useAnnotations, context.contents());

        return Arrays.asList(templateData);
    }

}
