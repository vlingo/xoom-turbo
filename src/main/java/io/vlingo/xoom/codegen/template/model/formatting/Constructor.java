// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.formatting;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateParameter;
import io.vlingo.xoom.codegen.template.model.valueobject.ValueObjectDetail;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;
import static io.vlingo.xoom.codegen.template.model.valueobject.ValueObjectDetail.valueObjectOf;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Constructor extends Formatters.Fields<List<String>> {

    private final String carrierName;
    private final TemplateParameter parameter;

    Constructor() {
        this("");
    }

    Constructor(final String carrierName) {
        this(carrierName, TemplateParameter.MEMBERS_ASSIGNMENT);
    }

    Constructor(final String carrierName, final TemplateParameter parameter) {
        this.parameter = parameter;
        this.carrierName = carrierName;
    }

    @Override
    public List<String> format(final CodeGenerationParameter aggregate,
                               final Stream<CodeGenerationParameter> fields) {
        if(parameter.equals(TemplateParameter.MEMBERS_ASSIGNMENT)) {
            return handleDefaultMembersAssignment(fields);
        } else if(parameter.equals(TemplateParameter.DATA_VALUE_OBJECT_ASSIGNMENT)) {
            return handleDataValueObjectAssignment(aggregate, fields.collect(toList()));
        }
        throw new IllegalStateException("Unable to format " + parameter);
    }

    private List<String> handleDefaultMembersAssignment(final Stream<CodeGenerationParameter> fields) {
        return fields.map(field -> {
            final String valueRetrievalExpression = resolveValueRetrieval(field);
            return String.format("this.%s = %s;", field.value, valueRetrievalExpression);
        }).collect(toList());
    }

    private List<String> handleDataValueObjectAssignment(final CodeGenerationParameter aggregate,
                                                         final List<CodeGenerationParameter> valueObjects) {
        return aggregate.retrieveAllRelated(STATE_FIELD).filter(ValueObjectDetail::isValueObject).map(stateField -> {
                    final String valueObjectType = stateField.retrieveRelatedValue(FIELD_TYPE);
                    final String dataValueObjectType = DATA_OBJECT.resolveClassname(valueObjectType);
                    final CodeGenerationParameter valueObject = valueObjectOf(valueObjectType, valueObjects.stream());
                    final String valueAccessExpression = resolveValueRetrieval(stateField);
                    final Function<CodeGenerationParameter, String> retrievalExpression =
                            field -> String.format("%s.%s", valueAccessExpression, field.value);

                    final String valueObjectFields =
                            valueObject.retrieveAllRelated(VALUE_OBJECT_FIELD).map(retrievalExpression).collect(joining(", "));

                    return String.format("this.%s = %s.of(%s);", stateField.value, dataValueObjectType, valueObjectFields);
                }).collect(toList());
    }

    private String resolveValueRetrieval(final CodeGenerationParameter field) {
        if(carrierName.isEmpty()) {
            return field.value;
        }
        return carrierName + "." + field.value;
    }

}
