// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.valueobject;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.MethodScope;
import io.vlingo.xoom.codegen.template.model.formatting.Formatters;

import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.parameter.Label.VALUE_OBJECT_FIELD;

public class ValueObjectConstructorInvocation implements Formatters.Arguments {

  @Override
  public String format(final CodeGenerationParameter valueObject, final MethodScope scope) {
    return valueObject.retrieveAllRelated(VALUE_OBJECT_FIELD)
            .map(field -> field.value).collect(Collectors.joining(", "));
  }
}
