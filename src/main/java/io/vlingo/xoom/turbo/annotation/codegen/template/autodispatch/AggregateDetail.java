// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.template.autodispatch;

import io.vlingo.xoom.turbo.annotation.codegen.template.Label;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.vlingo.xoom.turbo.codegen.designer.Label.*;

public class AggregateDetail {

  public static CodeGenerationParameter methodWithName(final CodeGenerationParameter aggregate, final String methodName) {
    return findMethod(aggregate, methodName).orElseThrow(() -> new IllegalArgumentException("Method " + methodName + " not found"));
  }

  private static Optional<CodeGenerationParameter> findMethod(final CodeGenerationParameter aggregate, final String methodName) {
    return aggregate.retrieveAllRelated(Label.AGGREGATE_METHOD)
            .filter(method -> methodName.equals(method.value) || method.value.startsWith(methodName + "("))
            .findFirst();
  }
}
