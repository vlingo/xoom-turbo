// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model;

import static io.vlingo.xoom.codegen.parameter.Label.STATE_FIELD;
import static io.vlingo.xoom.codegen.template.TemplateParameter.CONSTRUCTOR_PARAMETERS;
import static io.vlingo.xoom.codegen.template.TemplateParameter.EVENT_SOURCED;
import static io.vlingo.xoom.codegen.template.TemplateParameter.ID_TYPE;
import static io.vlingo.xoom.codegen.template.TemplateParameter.MEMBERS;
import static io.vlingo.xoom.codegen.template.TemplateParameter.MEMBERS_ASSIGNMENT;
import static io.vlingo.xoom.codegen.template.TemplateParameter.METHODS;
import static io.vlingo.xoom.codegen.template.TemplateParameter.METHOD_INVOCATION_PARAMETERS;
import static io.vlingo.xoom.codegen.template.TemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.STATE_NAME;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_STATE;
import static io.vlingo.xoom.codegen.template.model.AggregateArgumentsFormat.SIGNATURE_DECLARATION;
import static io.vlingo.xoom.codegen.template.model.AggregateFieldsFormat.ASSIGNMENT;
import static io.vlingo.xoom.codegen.template.model.AggregateFieldsFormat.DEFAULT_VALUE;
import static io.vlingo.xoom.codegen.template.model.AggregateFieldsFormat.MEMBER_DECLARATION;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.storage.StorageType;

public class AggregateStateTemplateData extends TemplateData {

    private final String protocolName;
    private final TemplateParameters parameters;

    @SuppressWarnings("unchecked")
    public AggregateStateTemplateData(final String packageName,
                                      final CodeGenerationParameter aggregate,
                                      final StorageType storageType) {
        this.protocolName = aggregate.value;
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, packageName)
                        .and(EVENT_SOURCED, storageType.isSourced())
                        .and(MEMBERS, MEMBER_DECLARATION.format(aggregate))
                        .and(MEMBERS_ASSIGNMENT, ASSIGNMENT.format(aggregate))
                        .and(ID_TYPE, StateFieldDetail.typeOf(aggregate, "id"))
                        .and(STATE_NAME, AGGREGATE_STATE.resolveClassname(protocolName))
                        .and(CONSTRUCTOR_PARAMETERS, SIGNATURE_DECLARATION.format(aggregate))
                        .and(METHOD_INVOCATION_PARAMETERS, resolveIdBasedConstructorParameters(aggregate))
                        .and(METHODS, new ArrayList<String>());

        this.dependOn(AggregateStateMethodTemplateData.from(aggregate));
    }

    private String resolveIdBasedConstructorParameters(final CodeGenerationParameter aggregate) {
        final CodeGenerationParameter idField = CodeGenerationParameter.of(STATE_FIELD, "id");
        return DEFAULT_VALUE.format(aggregate, Stream.of(idField));
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
        return AGGREGATE_STATE;
    }

}
