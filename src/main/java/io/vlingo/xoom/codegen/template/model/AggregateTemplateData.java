
// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.common.Completes;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.ArrayList;
import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.AGGREGATE_METHOD;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_STATE;

public class AggregateTemplateData extends TemplateData {

    private final String protocolName;
    private final TemplateParameters parameters;

    public AggregateTemplateData(final String packageName,
                                 final CodeGenerationParameter aggregate,
                                 final StorageType storageType) {
        this.protocolName = aggregate.value;
        this.parameters = TemplateParameters.with(PACKAGE_NAME, packageName)
                .and(AGGREGATE_PROTOCOL_NAME, protocolName)
                .and(STATE_NAME, AGGREGATE_STATE.resolveClassname(protocolName))
                .and(ENTITY_NAME, AGGREGATE.resolveClassname(protocolName))
                .and(ID_TYPE, StateFieldDetail.typeOf(aggregate, "id"))
                .and(METHODS, new ArrayList<String>())
                .and(STORAGE_TYPE, storageType);

        if(aggregate.hasAny(AGGREGATE_METHOD)) {
            this.parameters.addImport(Completes.class);
        }

        this.dependOn(AggregateMethodTemplateData.from(aggregate, storageType));
    }

    @Override
    public void handleDependencyOutcome(final TemplateStandard standard, final String outcome) {
        this.parameters.<List<String>>find(METHODS).add(outcome);
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
