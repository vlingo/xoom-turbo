// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.steps;


import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.TemplateData;
import io.vlingo.xoom.codegen.model.ModelTemplateDataFactory;
import io.vlingo.xoom.codegen.storage.StorageType;

import java.util.List;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;

public class ModelGenerationStep extends TemplateProcessingStep {

    @Override
    protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
        final String projectPath = context.projectPath();
        final String packageName = context.propertyOf(PACKAGE);
        final String aggregatesData = context.propertyOf(AGGREGATES);
        final StorageType storageType = StorageType.of(context.propertyOf(STORAGE_TYPE));
        return ModelTemplateDataFactory.build(packageName, projectPath,
                aggregatesData, storageType);
    }

    @Override
    public boolean shouldProcess(final CodeGenerationContext context) {
        return context.hasProperty(AGGREGATES);
    }

}
