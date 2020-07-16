// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.steps;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.TemplateData;
import io.vlingo.xoom.codegen.resource.RestResourceTemplateDataFactory;

import java.util.List;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.PACKAGE;
import static io.vlingo.xoom.codegen.CodeGenerationParameter.REST_RESOURCES;

public class RestResourceGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final String projectPath = context.projectPath();
        final String basePackage = context.propertyOf(PACKAGE);
        final String restResourcesData = context.propertyOf(REST_RESOURCES);
        return RestResourceTemplateDataFactory.build(basePackage, projectPath, restResourcesData);
    }

    @Override
    public boolean shouldProcess(final CodeGenerationContext context) {
        return context.hasProperty(REST_RESOURCES);
    }

}
