// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;


import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.template.storage.StorageType;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;

import java.util.List;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;

public class ModelGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final String packageName = context.parameterOf(PACKAGE);
        final String aggregatesData = context.parameterOf(AGGREGATES);
        final StorageType storageType = StorageType.of(context.parameterOf(STORAGE_TYPE));
        return ModelTemplateDataFactory.build(packageName, aggregatesData, storageType);
    }

    @Override
    public boolean shouldProcess(final CodeGenerationContext context) {
        return context.hasParameter(AGGREGATES);
    }

}
