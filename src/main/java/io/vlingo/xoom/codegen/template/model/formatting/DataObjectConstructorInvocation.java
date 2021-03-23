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

import java.beans.Introspector;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.*;

public class DataObjectConstructorInvocation implements Formatters.Arguments {

  @Override
  public String format(final CodeGenerationParameter parent, final MethodScope scope) {
    final String carrierName =
            scope.isStatic() ? Introspector.decapitalize(parent.value) : "";

    return parent.retrieveAllRelated(resolveFieldLabel(parent))
            .map(field -> resolveParameterName(carrierName, field))
            .collect(Collectors.joining(", "));
  }

  private Label resolveFieldLabel(final CodeGenerationParameter parent) {
    if(parent.isLabeled(AGGREGATE)) {
      return STATE_FIELD;
    }
    if(parent.isLabeled(VALUE_OBJECT)) {
      return VALUE_OBJECT_FIELD;
    }
    throw new IllegalArgumentException("Unable to format static method parameters from " + parent.label);
  }

  private String resolveParameterName(final String carrierName, final CodeGenerationParameter field) {
    if(carrierName.isEmpty()) {
      return field.value;
    }
    return carrierName + "." + field.value;
  }
}
