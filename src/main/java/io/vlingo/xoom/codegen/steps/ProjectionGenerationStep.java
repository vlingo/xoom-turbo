// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.steps;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.ProjectionType;
import io.vlingo.xoom.codegen.CodeGenerationParameter;
import io.vlingo.xoom.codegen.TemplateData;
import io.vlingo.xoom.codegen.projections.ProjectionTemplateDataFactory;

import java.util.List;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.AGGREGATES;
import static io.vlingo.xoom.codegen.CodeGenerationParameter.PROJECTIONS;

public class ProjectionGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final String projectPath = context.projectPath();
        final String basePackage = context.propertyOf(CodeGenerationParameter.PACKAGE);
        final ProjectionType projectionType = context.propertyOf(PROJECTIONS, ProjectionType::valueOf);
        return ProjectionTemplateDataFactory.build(basePackage, projectPath,
                        projectionType, context.contents());
    }

    @Override
    public boolean shouldProcess(final CodeGenerationContext context) {
        if(context.hasProperty(AGGREGATES)) {
            final ProjectionType projectionType =
                    context.propertyOf(PROJECTIONS, ProjectionType::valueOf);
            return projectionType.isProjectionEnabled();
        }
        return false;
    }

}
