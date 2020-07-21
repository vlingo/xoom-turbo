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
import static io.vlingo.xoom.codegen.template.TemplateStandard.STATE;

public class StateTemplateData extends TemplateData {

    private final String protocolName;
    private final TemplateParameters parameters;

    public StateTemplateData(final String packageName,
                             final String protocolName,
                             final StorageType storageType) {
        this.protocolName = protocolName;
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, packageName)
                        .and(STATE_NAME, STATE.resolveClassname(protocolName))
                        .and(STORAGE_TYPE, storageType);
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
        return STATE;
    }

}
