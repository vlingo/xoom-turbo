// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.bootstrap;


import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;

public class BootstrapGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final String basePackage = context.parameterOf(PACKAGE);
        final String artifactId = context.parameterOf(APPLICATION_NAME);
        final Boolean useCQRS = context.parameterOf(CQRS, Boolean::valueOf);
        final Boolean useAnnotations = context.parameterOf(ANNOTATIONS, Boolean::valueOf);
        final StorageType storageType = context.parameterOf(STORAGE_TYPE, StorageType::valueOf);
        final ProjectionType projectionType = context.parameterOf(PROJECTIONS, ProjectionType::valueOf);

        final TemplateData templateData =
                BootstrapTemplateData.from(basePackage, artifactId, storageType,
                        useCQRS, projectionType.isProjectionEnabled(), useAnnotations,
                        context.contents());

        return Arrays.asList(templateData);
    }

}
