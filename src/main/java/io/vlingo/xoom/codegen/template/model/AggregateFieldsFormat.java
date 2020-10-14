// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.AGGREGATE;
import static io.vlingo.xoom.codegen.parameter.Label.STATE_FIELD;
import static java.util.stream.Collectors.toList;

public interface AggregateFieldsFormat<T> {

    AggregateFieldsFormat<List<String>> ASSIGNMENT = new Constructor();
    AggregateFieldsFormat<List<String>> MEMBER_DECLARATION = new Member();
    AggregateFieldsFormat<List<String>> STATE_BASED_ASSIGNMENT = new Constructor("state");
    AggregateFieldsFormat<String> SELF_ALTERNATE_REFERENCE = AlternateReference.handlingSelfReferencedFields();
    AggregateFieldsFormat<String> DEFAULT_VALUE = AlternateReference.handlingDefaultFieldsValue();

    default T format(final CodeGenerationParameter aggregate) {
        return format(aggregate, aggregate.retrieveAll(STATE_FIELD));
    }

    T format(final CodeGenerationParameter parameter, final Stream<CodeGenerationParameter> fields);

    class Member implements AggregateFieldsFormat<List<String>> {

        private static final String PATTERN = "public final %s %s;";

        @Override
        public List<String> format(final CodeGenerationParameter aggregate,
                                   final Stream<CodeGenerationParameter> fields) {
            return fields.map(field -> {
                final String fieldType =
                        StateFieldType.retrieve(aggregate, field.value);
                return String.format(PATTERN, fieldType, field.value);
            }).collect(Collectors.toList());
        }
    }

    class Constructor implements AggregateFieldsFormat<List<String>> {

        private final String carrierName;
        private static final String PATTERN = "this.%s = %s;";

        protected Constructor() {
            this("");
        }

        protected Constructor(final String carrierName) {
            this.carrierName = carrierName;
        }

        @Override
        public List<String> format(final CodeGenerationParameter aggregate,
                                   final Stream<CodeGenerationParameter> fields) {
            return fields.map(field -> {
                final String valueRetrievalExpression = resolveValueRetrieval(field);
                return String.format(PATTERN, field.value, valueRetrievalExpression);
            }).collect(toList());
        }

        private String resolveValueRetrieval(final CodeGenerationParameter field) {
            if(carrierName.isEmpty()) {
                return field.value;
            }
            return carrierName + "." + field.value;
        }
    }

    class AlternateReference implements AggregateFieldsFormat<String> {

        private final Function<CodeGenerationParameter, String> absenceHandler;

        private AlternateReference(final Function<CodeGenerationParameter, String> absenceHandler) {
            this.absenceHandler = absenceHandler;
        }

        public static AlternateReference handlingSelfReferencedFields() {
            return new AlternateReference(field -> "this." + field.value);
        }

        public static AlternateReference handlingDefaultFieldsValue() {
            return new AlternateReference(field -> StateFieldType.resolveDefaultValue(field.parent(AGGREGATE), field.value));
        }

        @Override
        public String format(final CodeGenerationParameter para,
                             final Stream<CodeGenerationParameter> fields) {
            final List<CodeGenerationParameter> presentFields = fields.collect(toList());

            final Function<CodeGenerationParameter, String> mapper = field ->
                    isPresent(field, presentFields) ? field.value : absenceHandler.apply(field);

            return para.retrieveAll(STATE_FIELD).map(mapper).collect(Collectors.joining(", "));
        }

        private boolean isPresent(final CodeGenerationParameter field,
                                  final List<CodeGenerationParameter> presentFields) {
            return presentFields.stream().anyMatch(present -> present.value.equals(field.value));
        }

    }
}
