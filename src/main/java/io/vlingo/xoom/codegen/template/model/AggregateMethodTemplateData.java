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
import io.vlingo.xoom.codegen.template.storage.StorageType;

import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateParameter.STORAGE_TYPE;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.model.AggregateArgumentsFormat.METHOD_INVOCATION;
import static io.vlingo.xoom.codegen.template.model.AggregateArgumentsFormat.SIGNATURE_DECLARATION;
import static java.util.stream.Collectors.toList;

public class AggregateMethodTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final CodeGenerationParameter aggregate,
                                          final StorageType storageType) {
        return aggregate.retrieveAll(AGGREGATE_METHOD)
                .map(method -> new AggregateMethodTemplateData(method, storageType))
                .collect(toList());
    }

    private AggregateMethodTemplateData(final CodeGenerationParameter method,
                                        final StorageType storageType) {
        this.parameters =
                TemplateParameters.with(METHOD_NAME, method.value).and(STORAGE_TYPE, storageType)
                        .and(DOMAIN_EVENT_NAME, method.relatedParameterValueOf(DOMAIN_EVENT))
                        .and(METHOD_INVOCATION_PARAMETERS, METHOD_INVOCATION.format(method))
                        .and(METHOD_PARAMETERS, SIGNATURE_DECLARATION.format(method))
                        .and(SOURCED_EVENTS, SourcedEventParameter.from(method.parent()))
                        .and(STATE_NAME, TemplateStandard.AGGREGATE_STATE.resolveClassname(method.parent(AGGREGATE).value));
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return TemplateStandard.AGGREGATE_METHOD;
    }

}
