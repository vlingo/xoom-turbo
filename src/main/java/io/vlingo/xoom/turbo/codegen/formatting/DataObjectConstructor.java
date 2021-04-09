// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.turbo.codegen.formatting;

import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.parameter.Label;
import io.vlingo.xoom.turbo.codegen.template.TemplateStandard;
import io.vlingo.xoom.turbo.codegen.template.model.MethodScope;
import io.vlingo.xoom.turbo.codegen.template.model.valueobject.ValueObjectDetail;

import java.util.stream.Collectors;

import static io.vlingo.xoom.turbo.codegen.parameter.Label.*;

public class DataObjectConstructor implements Formatters.Arguments {

  @Override
  public String format(final CodeGenerationParameter parent, final MethodScope scope) {
    return parent.retrieveAllRelated(resolveFieldLabel(parent))
            .map(field -> String.format("final %s %s", resolveFieldType(field), field.value))
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

  private String resolveFieldType(final CodeGenerationParameter field) {
    final String fieldType = field.retrieveRelatedValue(FIELD_TYPE);
    if(ValueObjectDetail.isValueObject(field)) {
      return TemplateStandard.DATA_OBJECT.resolveClassname(fieldType);
    }
    return fieldType;
  }

}
