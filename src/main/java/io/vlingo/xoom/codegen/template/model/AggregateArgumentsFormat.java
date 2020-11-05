// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.AGGREGATE;
import static io.vlingo.xoom.codegen.parameter.Label.METHOD_PARAMETER;

public interface AggregateArgumentsFormat {

    AggregateArgumentsFormat METHOD_INVOCATION = new MethodInvocation("stage");
    AggregateArgumentsFormat SIGNATURE_DECLARATION = new SignatureDeclaration();

    default String format(final CodeGenerationParameter parameter) {
        return format(parameter, MethodScope.INSTANCE);
    }

    String format(final CodeGenerationParameter parameter, final MethodScope scope);

    class SignatureDeclaration implements AggregateArgumentsFormat {

        private static final String SIGNATURE_PATTERN = "final %s %s";
        private static final String STAGE_ARGUMENT = String.format(SIGNATURE_PATTERN, "Stage", "stage");

        @Override
        public String format(final CodeGenerationParameter parameter, final MethodScope scope) {
            final List<String> args = scope.isStatic() ?
                    Arrays.asList(STAGE_ARGUMENT) : Arrays.asList();

            return Stream.of(args, formatMethodParameters(parameter))
                    .flatMap(Collection::stream).collect(Collectors.joining(", "));
        }

        private List<String> formatMethodParameters(final CodeGenerationParameter parameter) {
            return parameter.retrieveAll(resolveFieldsLabel(parameter)).map(param -> {
                final String paramType = StateFieldDetail.typeOf(param.parent(AGGREGATE), param.value);
                return String.format(SIGNATURE_PATTERN, paramType, param.value);
            }).collect(Collectors.toList());
        }

        private Label resolveFieldsLabel(final CodeGenerationParameter parameter) {
            return parameter.isLabeled(AGGREGATE) ? Label.STATE_FIELD : Label.METHOD_PARAMETER;
        }
    }

    class MethodInvocation implements AggregateArgumentsFormat {

        private final String carrier;
        private final String stageVariableName;
        private static final String FIELD_ACCESS_PATTERN = "%s.%s";

        public MethodInvocation(final String stageVariableName) {
            this(stageVariableName, "");
        }

        public MethodInvocation(final String stageVariableName, final String carrier) {
            this.carrier = carrier;
            this.stageVariableName = stageVariableName;
        }

        @Override
        public String format(final CodeGenerationParameter method, final MethodScope scope) {
            final List<String> args = scope.isStatic() ?
                    Arrays.asList(stageVariableName) : Arrays.asList();

            return Stream.of(args, formatMethodParameters(method))
                    .flatMap(Collection::stream).collect(Collectors.joining(", "));
        }

        private List<String> formatMethodParameters(final CodeGenerationParameter method) {
            return method.retrieveAll(METHOD_PARAMETER).map(param -> carrier.isEmpty() ?
                    param.value : String.format(FIELD_ACCESS_PATTERN, carrier, param.value))
                    .collect(Collectors.toList());
        }

    }
}
