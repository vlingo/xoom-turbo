// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.formatting;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateParameter;

public class DefaultConstructorMembersAssignment extends Formatters.Fields<List<String>> {

    private final String carrierName;
    private final TemplateParameter parameter; // TODO: Why unused? Remove?

    DefaultConstructorMembersAssignment() {
        this("");
    }

    DefaultConstructorMembersAssignment(final String carrierName) {
        this(carrierName, TemplateParameter.MEMBERS_ASSIGNMENT);
    }

    DefaultConstructorMembersAssignment(final String carrierName, final TemplateParameter parameter) {
        this.parameter = parameter;
        this.carrierName = carrierName;
    }

    @Override
    public List<String> format(final CodeGenerationParameter aggregate,
                               final Stream<CodeGenerationParameter> fields) {
        return fields.map(field -> {
            final String valueRetrievalExpression = resolveValueRetrieval(field);
            return String.format("this.%s = %s;", field.value, valueRetrievalExpression);
        }).collect(toList());
    }

    private String resolveValueRetrieval(final CodeGenerationParameter field) {
        if(carrierName.isEmpty()) {
            return field.value;
        }
        return carrierName + "." + field.value;
    }

}
