// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.formatting;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.MethodScope;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.METHOD_PARAMETER;

public class MethodInvocation implements AggregateArgumentsFormat {

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
        return method.retrieveAllRelated(METHOD_PARAMETER).map(param -> carrier.isEmpty() ?
                param.value : String.format(FIELD_ACCESS_PATTERN, carrier, param.value))
                .collect(Collectors.toList());
    }

}
