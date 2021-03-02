// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.formatting;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.model.MethodScope;
import io.vlingo.xoom.codegen.template.model.StateFieldDetail;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.AGGREGATE;

public class SignatureDeclaration implements AggregateArgumentsFormat {

    private static final String SIGNATURE_PATTERN = "final %s %s";
    private static final String STAGE_ARGUMENT = String.format(SIGNATURE_PATTERN, "Stage", "stage");

    SignatureDeclaration() {}

    @Override
    public String format(final CodeGenerationParameter parameter, final MethodScope scope) {
        final List<String> args = scope.isStatic() ?
                Arrays.asList(STAGE_ARGUMENT) : Arrays.asList();

        return Stream.of(args, formatMethodParameters(parameter))
                .flatMap(Collection::stream).collect(Collectors.joining(", "));
    }

    private List<String> formatMethodParameters(final CodeGenerationParameter parameter) {
        return parameter.retrieveAllRelated(resolveFieldsLabel(parameter)).map(param -> {
            final String paramType = StateFieldDetail.typeOf(param.parent(AGGREGATE), param.value);
            return String.format(SIGNATURE_PATTERN, paramType, param.value);
        }).collect(Collectors.toList());
    }

    private Label resolveFieldsLabel(final CodeGenerationParameter parameter) {
        return parameter.isLabeled(AGGREGATE) ? Label.STATE_FIELD : Label.METHOD_PARAMETER;
    }
}
