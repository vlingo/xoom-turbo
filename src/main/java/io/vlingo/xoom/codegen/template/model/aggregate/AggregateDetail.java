// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model.aggregate;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.model.FieldDetail;
import io.vlingo.xoom.codegen.template.model.valueobject.ValueObjectDetail;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.METHOD_PARAMETER;
import static io.vlingo.xoom.codegen.parameter.Label.STATE_FIELD;

public class AggregateDetail {

    public static String resolvePackage(final String basePackage, final String aggregateProtocolName) {
        return String.format("%s.%s.%s", basePackage, "model", aggregateProtocolName).toLowerCase();
    }

    public static CodeGenerationParameter stateFieldWithName(final CodeGenerationParameter aggregate, final String fieldName) {
        return aggregate.retrieveAllRelated(Label.STATE_FIELD).filter(field -> field.value.equals(fieldName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Field " + fieldName + " not found" ));
    }

    public static CodeGenerationParameter methodWithName(final CodeGenerationParameter aggregate, final String methodName) {
        return findMethod(aggregate, methodName).orElseThrow(() -> new IllegalArgumentException("Method " + methodName + " not found" ));
    }

    public static CodeGenerationParameter eventWithName(final CodeGenerationParameter aggregate, final String eventName) {
        return aggregate.retrieveAllRelated(Label.DOMAIN_EVENT).filter(event -> event.value.equals(eventName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Event " + eventName + " not found" ));
    }

    public static List<CodeGenerationParameter> findAggregatesWithValueObjects(final Stream<CodeGenerationParameter> aggregates) {
        return aggregates.filter(aggregate -> ValueObjectDetail.useValueObject(aggregate)).collect(Collectors.toList());
    }

    public static Stream<CodeGenerationParameter> findInvolvedStateFields(final CodeGenerationParameter aggregate, final String methodName) {
        final CodeGenerationParameter method = methodWithName(aggregate, methodName);
        final Stream<CodeGenerationParameter> methodParameters = method.retrieveAllRelated(METHOD_PARAMETER);
        return methodParameters.map(parameter -> stateFieldWithName(aggregate, parameter.value));
    }

    public static Stream<CodeGenerationParameter> findScalarStateFields(final CodeGenerationParameter aggregate) {
        return aggregate.retrieveAllRelated(STATE_FIELD).filter(FieldDetail::isScalar);
    }

    public static Stream<CodeGenerationParameter> findValueObjects(final CodeGenerationParameter aggregate) {
        return aggregate.retrieveAllRelated(STATE_FIELD).filter(ValueObjectDetail::isValueObject);
    }

    private static Optional<CodeGenerationParameter> findMethod(final CodeGenerationParameter aggregate, final String methodName) {
        return aggregate.retrieveAllRelated(Label.AGGREGATE_METHOD)
                .filter(method -> methodName.equals(method.value) || method.value.startsWith(methodName + "("))
                .findFirst();
    }
}
