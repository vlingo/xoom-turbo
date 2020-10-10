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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface AggregateArgumentsFormat {

    AggregateArgumentsFormat METHOD_INVOCATION = new MethodInvocation();
    AggregateArgumentsFormat SIGNATURE_DECLARATION = new SignatureDeclaration();

    String format(final CodeGenerationParameter parameter);

    String format(final CodeGenerationParameter parameter, final MethodScope scope);

    class SignatureDeclaration implements AggregateArgumentsFormat {

        private static final String SIGNATURE_PATTERN = "final %s %s";
        private static final String STAGE_ARGUMENT = String.format(SIGNATURE_PATTERN, "Stage", "stage");

        @Override
        public String format(final CodeGenerationParameter parameter) {
            return format(parameter, MethodScope.INSTANCE);
        }

        @Override
        public String format(final CodeGenerationParameter parameter, final MethodScope scope) {
            final List<String> args = scope.isStatic() ?
                    Arrays.asList(STAGE_ARGUMENT) : Arrays.asList();

            return Stream.of(args, formatMethodParameters(parameter))
                    .flatMap(Collection::stream).collect(Collectors.joining(", "));
        }

        private Set<String> formatMethodParameters(final CodeGenerationParameter parameter) {
            return parameter.retrieveAll(resolveFieldsLabel(parameter)).map(param -> {
                final String paramType = StateFieldTypeRetriever.retrieve(param.parent(), param.value);
                return String.format(SIGNATURE_PATTERN, param.value, paramType);
            }).collect(Collectors.toSet());
        }

        private Label resolveFieldsLabel(final CodeGenerationParameter parameter) {
            return parameter.isLabeled(Label.AGGREGATE) ? Label.STATE_FIELD : Label.METHOD_PARAMETER;
        }
    }

    class MethodInvocation implements AggregateArgumentsFormat {

        @Override
        public String format(final CodeGenerationParameter method) {
            return method.retrieveAll(Label.METHOD_PARAMETER).map(param -> param.value).collect(Collectors.joining(", "));
        }

        @Override
        public String format(final CodeGenerationParameter method, final MethodScope scope) {
            throw new UnsupportedOperationException("This format does not support MethodScope");
        }

    }
}
