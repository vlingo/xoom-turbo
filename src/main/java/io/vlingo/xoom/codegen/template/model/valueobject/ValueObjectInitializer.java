// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model.valueobject;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail;
import io.vlingo.xoom.codegen.template.model.formatting.Formatters;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.*;

public class ValueObjectInitializer extends Formatters.Fields<List<String>> {

  private final String carrierName;

  public ValueObjectInitializer(final String carrierName) {
    this.carrierName = carrierName;
  }

  @Override
  public List<String> format(final CodeGenerationParameter method,
                             final Stream<CodeGenerationParameter> fields) {
    final CodeGenerationParameter aggregate = method.parent(AGGREGATE);
    final List<CodeGenerationParameter> valueObjects = fields.collect(Collectors.toList());
    return method.retrieveAllRelated(METHOD_PARAMETER).map(param -> AggregateDetail.stateFieldWithName(aggregate, param.value))
            .filter(field -> ValueObjectDetail.isValueObject(field)).map(field -> concatenateInitializer(field, valueObjects))
            .collect(Collectors.toList());
  }

  private String concatenateInitializer(final CodeGenerationParameter stateField, final List<CodeGenerationParameter> valueObjects) {
    final String fieldType = stateField.retrieveRelatedValue(FIELD_TYPE);
    final CodeGenerationParameter valueObject = ValueObjectDetail.valueObjectOf(fieldType, valueObjects.stream());
    final Function<CodeGenerationParameter, String> formatter =
            valueObjectField -> String.format("%s.%s.%s", carrierName, stateField.value, valueObjectField.value);
    final String args = valueObject.retrieveAllRelated(VALUE_OBJECT_FIELD).map(formatter).collect(Collectors.joining(", "));
    return String.format("final %s %s = %s.of(%s);", valueObject.value, stateField.value, valueObject.value, args);
  }
}
