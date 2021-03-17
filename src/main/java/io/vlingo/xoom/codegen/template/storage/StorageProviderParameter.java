// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;


import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.projections.ProjectionType;

import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;

public class StorageProviderParameter {

    private final String name;
    private final boolean useProjections;

    private StorageProviderParameter(final TemplateParameters parameters) {
        this.name = parameters.find(STORE_PROVIDER_NAME);
        final ProjectionType projectionType = parameters.find(PROJECTION_TYPE);
        final Model model = parameters.find(MODEL);
        this.useProjections = projectionType.isProjectionEnabled() && model.isCommandModel();
    }

    public static List<StorageProviderParameter> from(final List<TemplateData> templateData) {
        return templateData.stream()
                .map(data -> new StorageProviderParameter(data.parameters()))
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public boolean getUseProjections() {
        return useProjections;
    }

}
