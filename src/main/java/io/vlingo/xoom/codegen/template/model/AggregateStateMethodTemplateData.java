// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.AGGREGATE_METHOD;
import static io.vlingo.xoom.codegen.parameter.Label.METHOD_PARAMETER;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_STATE;
import static io.vlingo.xoom.codegen.template.model.AggregateArgumentsFormat.SIGNATURE_DECLARATION;
import static io.vlingo.xoom.codegen.template.model.AggregateFieldsFormat.SELF_ALTERNATE_REFERENCE;
import static java.util.stream.Collectors.toList;

public class AggregateStateMethodTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final CodeGenerationParameter aggregate) {
        return aggregate.retrieveAll(AGGREGATE_METHOD)
                .map(method -> new AggregateStateMethodTemplateData(aggregate, method))
                .collect(toList());
    }

    private AggregateStateMethodTemplateData(final CodeGenerationParameter aggregate,
                                             final CodeGenerationParameter method) {
        this.parameters =
                TemplateParameters.with(METHOD_NAME, method.value)
                        .and(METHOD_PARAMETERS, SIGNATURE_DECLARATION.format(method))
                        .and(CONSTRUCTOR_PARAMETERS, resolveConstructorParameters(method))
                        .and(STATE_NAME, AGGREGATE_STATE.resolveClassname(aggregate.value));
    }

    private String resolveConstructorParameters(final CodeGenerationParameter method) {
        return SELF_ALTERNATE_REFERENCE.format(method.parent(), method.retrieveAll(METHOD_PARAMETER));
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.AGGREGATE_STATE_METHOD;
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

}
