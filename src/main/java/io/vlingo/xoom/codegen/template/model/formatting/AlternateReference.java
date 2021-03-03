// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.formatting;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.aggregate.FieldDetail;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.AGGREGATE;
import static io.vlingo.xoom.codegen.parameter.Label.STATE_FIELD;
import static java.util.stream.Collectors.toList;

public class AlternateReference extends Formatters.Fields<String> {

    private final Function<CodeGenerationParameter, String> absenceHandler;

    private AlternateReference(final Function<CodeGenerationParameter, String> absenceHandler) {
        this.absenceHandler = absenceHandler;
    }

    static AlternateReference handlingSelfReferencedFields() {
        return new AlternateReference(field -> "this." + field.value);
    }

    static AlternateReference handlingDefaultFieldsValue() {
        return new AlternateReference(field -> FieldDetail.resolveDefaultValue(field.parent(AGGREGATE), field.value));
    }

    @Override
    public String format(final CodeGenerationParameter para,
                         final Stream<CodeGenerationParameter> fields) {
        final List<CodeGenerationParameter> presentFields = fields.collect(toList());

        final Function<CodeGenerationParameter, String> mapper = field ->
                isPresent(field, presentFields) ? field.value : absenceHandler.apply(field);

        return para.retrieveAllRelated(STATE_FIELD).map(mapper).collect(Collectors.joining(", "));
    }

    private boolean isPresent(final CodeGenerationParameter field,
                              final List<CodeGenerationParameter> presentFields) {
        return presentFields.stream().anyMatch(present -> present.value.equals(field.value));
    }
}
