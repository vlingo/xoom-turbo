// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model.valueobject;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail;

import java.util.List;

public class ValueObjectDetail {

  public static String resolvePackage(final String basePackage,
                                      final CodeGenerationParameter valueObject,
                                      final List<CodeGenerationParameter> aggregates) {
    final List<CodeGenerationParameter> dependentAggregates = aggregates;
    final String defaultPackage = String.format("%s.%s", basePackage, "model");
    if(dependentAggregates.size() == 1) {
      return AggregateDetail.resolvePackage(basePackage, aggregates.get(0).value);
    }
    return defaultPackage;
  }

}
