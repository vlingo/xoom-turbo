// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model.valueobject;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.FieldDetail;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.content.ContentQuery.findFullyQualifiedClassName;
import static io.vlingo.xoom.codegen.parameter.Label.FIELD_TYPE;
import static io.vlingo.xoom.codegen.parameter.Label.STATE_FIELD;
import static io.vlingo.xoom.codegen.template.TemplateStandard.VALUE_OBJECT;

public class ValueObjectDetail {

  public static String resolvePackage(final String basePackage,
                                      final String valueObject,
                                      final List<CodeGenerationParameter> aggregates) {
    final List<CodeGenerationParameter> dependentAggregates =
            aggregates.stream().filter(aggregate -> aggregate.retrieveAllRelated(STATE_FIELD)
            .anyMatch(field -> field.retrieveRelatedValue(FIELD_TYPE).equals(valueObject)))
            .collect(Collectors.toList());

    if(dependentAggregates.size() == 1) {
      return AggregateDetail.resolvePackage(basePackage, aggregates.get(0).value);
    }

    return String.format("%s.%s", basePackage, "model");
  }

  public static boolean useValueObject(final CodeGenerationParameter aggregate) {
    return aggregate.retrieveAllRelated(STATE_FIELD).anyMatch(ValueObjectDetail::isValueObject);
  }

  public static Set<String> retrieveQualifiedNames(final List<Content> contexts,
                                                   final Stream<CodeGenerationParameter> arguments) {
    return arguments.filter(ValueObjectDetail::isValueObject)
            .map(arg -> arg.retrieveRelatedValue(FIELD_TYPE))
            .map(valueObjectName -> findFullyQualifiedClassName(VALUE_OBJECT, valueObjectName, contexts))
            .collect(Collectors.toSet());
  }

  private static boolean isValueObject(final CodeGenerationParameter field) {
    return !FieldDetail.scalarTyped(field);
  }

}
