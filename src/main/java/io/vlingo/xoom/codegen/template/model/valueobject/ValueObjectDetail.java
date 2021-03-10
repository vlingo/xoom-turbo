// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model.valueobject;

import io.vlingo.xoom.codegen.content.ClassFormatter;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.model.FieldDetail;
import io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.content.ContentQuery.findFullyQualifiedClassName;
import static io.vlingo.xoom.codegen.parameter.Label.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.VALUE_OBJECT;
import static io.vlingo.xoom.codegen.template.model.aggregate.AggregateDetail.eventWithName;

public class ValueObjectDetail {

  public static String resolvePackage(final String basePackage) {
    return String.format("%s.%s", basePackage, "model");
  }

  public static Set<String> resolveImports(final List<Content> contents,
                                           final Stream<CodeGenerationParameter> arguments) {
    final Optional<String> anyQualifiedName =
            arguments.filter(ValueObjectDetail::isValueObject)
                    .map(arg -> arg.retrieveRelatedValue(FIELD_TYPE))
                    .map(valueObjectName -> findFullyQualifiedClassName(VALUE_OBJECT, valueObjectName, contents))
                    .findAny();

    if(anyQualifiedName.isPresent()) {
      final String packageName = ClassFormatter.packageOf(anyQualifiedName.get());
      return Stream.of(ClassFormatter.importAllFrom(packageName)).collect(Collectors.toSet());
    }

    return Collections.emptySet();
  }

  public static Stream<CodeGenerationParameter> orderByDependency(final Stream<CodeGenerationParameter> valueObjects) {
    return ValueObjectDependencyOrder.sort(valueObjects);
  }

  public static Stream<CodeGenerationParameter> findPublishedValueObjects(final List<CodeGenerationParameter> exchanges,
                                                                          final List<CodeGenerationParameter> valueObjects) {
    return exchanges.stream().filter(exchange -> exchange.hasAny(DOMAIN_EVENT))
            .flatMap(event -> event.retrieveAllRelated(DOMAIN_EVENT))
            .map(event -> eventWithName(event.parent(AGGREGATE), event.value))
            .flatMap(event -> event.retrieveAllRelated(STATE_FIELD))
            .map(stateField -> AggregateDetail.stateFieldWithName(stateField.parent(AGGREGATE), stateField.value))
            .filter(ValueObjectDetail::isValueObject)
            .map(field -> field.retrieveRelatedValue(FIELD_TYPE))
            .distinct().map(type -> valueObjectOf(type, valueObjects.stream()));
  }

  public static CodeGenerationParameter valueObjectOf(final String valueObjectType,
                                                      final Stream<CodeGenerationParameter> valueObjects) {
      return valueObjects.filter(valueObject -> valueObject.value.equals(valueObjectType)).findFirst()
              .orElseThrow(() -> new IllegalArgumentException("Unable to find " + valueObjectType));
  }

  public static Stream<CodeGenerationParameter> collectRelatedValueObjects(final CodeGenerationParameter valueObject,
                                                                           final List<CodeGenerationParameter> valueObjects) {
    final List<CodeGenerationParameter> related = new ArrayList<>();
    findRelatedValueObjects(valueObject, valueObjects).forEach(vo -> collectRelatedValueObjects(vo, valueObjects, related));
    return related.stream();
  }

  private static void collectRelatedValueObjects(final CodeGenerationParameter valueObject,
                                                 final List<CodeGenerationParameter> valueObjects,
                                                 final List<CodeGenerationParameter> related) {
    if(related.stream().noneMatch(vo -> vo.value.equals(valueObject.value))) {
      related.add(valueObject);
    }
    findRelatedValueObjects(valueObject, valueObjects).forEach(vo -> collectRelatedValueObjects(vo, valueObjects, related));
  }

  private static Stream<CodeGenerationParameter> findRelatedValueObjects(final CodeGenerationParameter valueObject,
                                                                         final List<CodeGenerationParameter> valueObjects) {
    return valueObject.retrieveAllRelated(VALUE_OBJECT_FIELD).filter(ValueObjectDetail::isValueObject)
            .map(valueObjectField -> valueObjectField.retrieveRelatedValue(FIELD_TYPE))
            .map(valueObjectType -> valueObjectOf(valueObjectType, valueObjects.stream()));
  }

  public static boolean useValueObject(final CodeGenerationParameter aggregate) {
    return aggregate.retrieveAllRelated(STATE_FIELD).anyMatch(ValueObjectDetail::isValueObject);
  }

  public static boolean isValueObject(final CodeGenerationParameter field) {
    return !FieldDetail.isScalar(field);
  }

}
