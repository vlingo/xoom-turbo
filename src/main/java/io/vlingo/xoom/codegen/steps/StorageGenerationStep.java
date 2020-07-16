// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.steps;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationParameter;
import io.vlingo.xoom.codegen.ProjectionType;
import io.vlingo.xoom.codegen.TemplateData;
import io.vlingo.xoom.codegen.storage.StorageTemplateDataFactory;
import io.vlingo.xoom.codegen.storage.StorageType;

import java.util.List;

public class StorageGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final String projectPath = context.projectPath();
        final String basePackage = context.parameterOf(CodeGenerationParameter.PACKAGE);
        final Boolean enableCQRS = context.parameterOf(CodeGenerationParameter.CQRS, Boolean::valueOf);
        final StorageType storageType = context.parameterOf(CodeGenerationParameter.STORAGE_TYPE, StorageType::of);
        final ProjectionType projectionType = context.parameterOf(CodeGenerationParameter.PROJECTIONS, ProjectionType::valueOf);
        return StorageTemplateDataFactory.build(basePackage, projectPath, enableCQRS,
                        context.contents(), storageType, context.databases(), projectionType);
    }

}
