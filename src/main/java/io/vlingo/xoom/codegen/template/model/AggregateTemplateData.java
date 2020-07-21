// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.template.storage.StorageType;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class AggregateTemplateData extends TemplateData {

    private final String protocolName;
    private final TemplateParameters parameters;

    public AggregateTemplateData(final String packageName,
                                 final String protocolName,
                                 final StorageType storageType) {
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, packageName)
                        .and(AGGREGATE_PROTOCOL_NAME, protocolName)
                        .and(STATE_NAME, STATE.resolveClassname(protocolName))
                        .and(ENTITY_NAME, AGGREGATE.resolveClassname(protocolName))
                        .and(PLACEHOLDER_EVENT, true).and(STORAGE_TYPE, storageType)
                        .andResolve(DOMAIN_EVENT_NAME, param -> DOMAIN_EVENT.resolveClassname(protocolName, param));

        this.protocolName = protocolName;
    }

    @Override
    public String filename() {
        return standard().resolveFilename(protocolName, parameters);
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return AGGREGATE;
    }

}
