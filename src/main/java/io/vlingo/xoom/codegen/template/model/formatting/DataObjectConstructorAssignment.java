// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.formatting;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.valueobject.ValueObjectDetail;

import java.beans.Introspector;
import java.util.List;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;
import static java.util.stream.Collectors.toList;

public class DataObjectConstructorAssignment extends Formatters.Fields<List<String>> {

  @Override
  public List<String> format(final CodeGenerationParameter carrier,
                             final Stream<CodeGenerationParameter> fields) {
    return carrier.retrieveAllRelated(resolveFieldLabel(carrier)).map(field -> {
      final String fieldAccessExpression = resolveFieldAccessExpression(field);
      return ValueObjectDetail.isValueObject(field) ?
              formatValueObjectAssignment(field, fieldAccessExpression) :
              formatScalarTypeAssignment(field, fieldAccessExpression);
    }).collect(toList());
  }

  private String formatScalarTypeAssignment(final CodeGenerationParameter field, final String fieldAccessExpression) {
    return String.format("this.%s = %s;", field.value, fieldAccessExpression);
  }

  private String formatValueObjectAssignment(final CodeGenerationParameter field, final String fieldAccessExpression) {
    final String valueObjectType = field.retrieveRelatedValue(FIELD_TYPE);
    final String dataValueObjectType = TemplateStandard.DATA_OBJECT.resolveClassname(valueObjectType);
    return String.format("this.%s = %s != null ? %s.of(%s) : null;",
            field.value, fieldAccessExpression, dataValueObjectType, fieldAccessExpression);
  }

  private Label resolveFieldLabel(final CodeGenerationParameter carrier) {
    if(carrier.isLabeled(AGGREGATE)) {
      return STATE_FIELD;
    }
    if(carrier.isLabeled(VALUE_OBJECT)) {
      return VALUE_OBJECT_FIELD;
    }
    throw new UnsupportedOperationException("Unable to format fields assignment from " + carrier.label);
  }

  private String resolveCarrierName(final CodeGenerationParameter field) {
    if(field.isLabeled(STATE_FIELD)) {
      return "state";
    }
    if(field.isLabeled(VALUE_OBJECT_FIELD)) {
      return Introspector.decapitalize(field.parent(VALUE_OBJECT).value);
    }
    throw new UnsupportedOperationException("Unable to format fields assignment labeled as " + field.label);
  }

  private String resolveFieldAccessExpression(final CodeGenerationParameter field){
    return resolveCarrierName(field) + "." + field.value;
  }
}
