// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;

import java.util.Optional;

public class AggregateDetail {

    public static CodeGenerationParameter methodWithName(final CodeGenerationParameter aggregate, final String methodName) {
        return findMethod(aggregate, methodName).orElseThrow(() -> new IllegalArgumentException("Method " + methodName + " not found" ));
    }

    private static Optional<CodeGenerationParameter> findMethod(final CodeGenerationParameter aggregate, final String methodName) {
        return aggregate.retrieveAllRelated(Label.AGGREGATE_METHOD)
                .filter(method -> methodName.equals(method.value) || method.value.startsWith(methodName + "("))
                .findFirst();
    }
}
