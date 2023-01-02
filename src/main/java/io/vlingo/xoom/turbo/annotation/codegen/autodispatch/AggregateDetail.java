// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.autodispatch;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.annotation.codegen.Label;

import java.util.List;
import java.util.Optional;

import static io.vlingo.xoom.turbo.annotation.codegen.autodispatch.RouteDetail.extractCompositeIdFrom;
import static java.util.stream.Collectors.toList;

public class AggregateDetail {
  private static final String COMPOSITE_ID_DECLARATION_PATTERN = "final String";

  public static CodeGenerationParameter methodWithName(final CodeGenerationParameter aggregate, final String methodName) {
    return findMethod(aggregate, methodName).orElseThrow(() -> new IllegalArgumentException("Method " + methodName + " not found"));
  }

  public static String resolveCompositeIdFields(CodeGenerationParameter aggregate) {
    final String routePath = aggregate.retrieveRelatedValue(Label.URI_ROOT);

    final List<String> compositeIdFields = extractCompositeIdFrom(routePath)
        .stream()
        .map(field -> String.format("%s %s", COMPOSITE_ID_DECLARATION_PATTERN, field))
        .collect(toList());

    if(compositeIdFields.isEmpty())
      return "";

    return String.format("%s, ", String.join(", ", compositeIdFields));
  }
  private static Optional<CodeGenerationParameter> findMethod(final CodeGenerationParameter aggregate, final String methodName) {
    return aggregate.retrieveAllRelated(Label.AGGREGATE_METHOD)
            .filter(method -> methodName.equals(method.value) || method.value.startsWith(methodName + "("))
            .findFirst();
  }
}
