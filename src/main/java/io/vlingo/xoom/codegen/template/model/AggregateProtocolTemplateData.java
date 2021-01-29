// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import static io.vlingo.xoom.codegen.template.TemplateParameter.AGGREGATE_PROTOCOL_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.METHODS;
import static io.vlingo.xoom.codegen.template.TemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

public class AggregateProtocolTemplateData extends TemplateData {

    private final String protocolName;
    private final TemplateParameters parameters;

    @SuppressWarnings("unchecked")
    public AggregateProtocolTemplateData(final String packageName,
                                         final CodeGenerationParameter aggregate) {
        this.protocolName = aggregate.value;
        this.parameters = TemplateParameters.with(PACKAGE_NAME, packageName)
                .and(AGGREGATE_PROTOCOL_NAME, aggregate.value)
                .and(METHODS, new ArrayList<String>());

        this.dependOn(AggregateProtocolMethodTemplateData.from(parameters, aggregate));
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
        return AGGREGATE_PROTOCOL;
    }

}
