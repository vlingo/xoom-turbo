// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.storage;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.Map;

import static io.vlingo.xoom.codegen.template.TemplateParameter.RESOURCE_FILE;

public class DatabasePropertiesTemplateData extends TemplateData {

    private final TemplateParameters templateParameters;

    public DatabasePropertiesTemplateData(final Map<Model, DatabaseType> databases) {
        this.templateParameters =
                loadParameters(databases);
    }

    private TemplateParameters loadParameters(final Map<Model, DatabaseType> databases) {
        final TemplateParameters parameters =
                TemplateParameters.with(RESOURCE_FILE, true);

        databases.entrySet().forEach(entry -> {
            final TemplateParameter parameter =
                    entry.getKey().isQueryModel() ?
                            TemplateParameter.QUERY_DATABASE_PARAMETER :
                            TemplateParameter.DEFAULT_DATABASE_PARAMETER;

            parameters.and(parameter, new DatabaseParameter(entry.getValue()));
        });

        return parameters;
    }

    @Override
    public TemplateParameters parameters() {
        return templateParameters;
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.DATABASE_PROPERTIES;
    }
}
