// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.projections;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.CodeGenerationParameter;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.AGGREGATES;
import static io.vlingo.xoom.codegen.CodeGenerationParameter.PROJECTIONS;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;

public class ProjectionGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final String basePackage = context.parameterOf(CodeGenerationParameter.PACKAGE);
        final ProjectionType projectionType = context.parameterOf(PROJECTIONS, ProjectionType::valueOf);
        return ProjectionTemplateDataFactory.build(basePackage, projectionType, context.contents());
    }

    @Override
    public boolean shouldProcess(final CodeGenerationContext context) {
        if(ContentQuery.exists(AGGREGATE_PROTOCOL, context.contents())) {
            final ProjectionType projectionType =
                    context.parameterOf(PROJECTIONS, ProjectionType::valueOf);
            return projectionType.isProjectionEnabled();
        }
        return false;
    }

}
