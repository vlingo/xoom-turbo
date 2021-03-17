// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.formatting;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.model.FieldDetail;
import io.vlingo.xoom.codegen.template.model.MethodScope;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;

public class SignatureDeclaration implements Formatters.Arguments {

    private static final String SIGNATURE_PATTERN = "final %s %s";
    private static final String STAGE_ARGUMENT = String.format(SIGNATURE_PATTERN, "Stage", "stage");

    SignatureDeclaration() {}

    @Override
    public String format(final CodeGenerationParameter parameter, final MethodScope scope) {
        final List<String> args = scope.isStatic() ?
                Arrays.asList(STAGE_ARGUMENT) : Arrays.asList();

        return Stream.of(args, collectMethodParameters(parameter))
                .flatMap(Collection::stream).collect(Collectors.joining(", "));
    }

    private List<String> collectMethodParameters(final CodeGenerationParameter parameter) {
        switch (parameter.label) {
            case AGGREGATE:
                return collectStateFields(parameter);
            case AGGREGATE_METHOD:
                return formatMethodParameters(parameter);
            case VALUE_OBJECT:
                return formatValueObjectFields(parameter);
            default:
                throw new UnsupportedOperationException("Unable to format fields of " + parameter.label);
        }
    }

    private List<String> collectStateFields(final CodeGenerationParameter aggregate) {
        return applyAggregateBasedFormatting(aggregate, STATE_FIELD);
    }

    private List<String> formatMethodParameters(final CodeGenerationParameter aggregateMethod) {
        return applyAggregateBasedFormatting(aggregateMethod, METHOD_PARAMETER);
    }

    private List<String> applyAggregateBasedFormatting(final CodeGenerationParameter parent,
                                                       final Label relatedParameter) {
        return parent.retrieveAllRelated(relatedParameter).map(field -> {
            final String fieldType = FieldDetail.typeOf(field.parent(AGGREGATE), field.value);
            return String.format(SIGNATURE_PATTERN, fieldType, field.value);
        }).collect(Collectors.toList());
    }

    private List<String> formatValueObjectFields(final CodeGenerationParameter valueObject) {
        return valueObject.retrieveAllRelated(VALUE_OBJECT_FIELD).map(param -> {
            final String fieldType = param.retrieveRelatedValue(FIELD_TYPE);
            return String.format(SIGNATURE_PATTERN, fieldType, param.value);
        }).collect(Collectors.toList());
    }

}
