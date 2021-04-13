// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template.unittest.queries;

import io.vlingo.xoom.turbo.codegen.formatting.NumberFormat;
import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.turbo.codegen.parameter.Label;
import io.vlingo.xoom.turbo.codegen.template.model.FieldDetail;
import io.vlingo.xoom.turbo.codegen.template.model.valueobject.ValueObjectDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static io.vlingo.xoom.turbo.codegen.parameter.Label.FIELD_TYPE;
import static io.vlingo.xoom.turbo.codegen.parameter.Label.VALUE_OBJECT_FIELD;
import static java.util.stream.Collectors.toList;

public class TestDataValueGenerator {

  private final int dataSetSize;
  private final String valuePrefix;
  private final String aggregateName;
  private final TestDataValues generatedValues = new TestDataValues();
  private final List<CodeGenerationParameter> stateFields = new ArrayList<>();
  private final List<CodeGenerationParameter> valueObjects = new ArrayList<>();
  private boolean currentBooleanValue;
  private int currentNumericValue;

  public static TestDataValueGenerator with(final int dataSetSize,
                                            final String valuePrefix,
                                            final CodeGenerationParameter aggregate,
                                            final List<CodeGenerationParameter> valueObjects) {
    final List<CodeGenerationParameter> stateFields =
            aggregate.retrieveAllRelated(Label.STATE_FIELD).collect(toList());

    return with(dataSetSize, valuePrefix, aggregate.value, stateFields, valueObjects);
  }

  public static TestDataValueGenerator with(final int dataSetSize,
                                            final String valuePrefix,
                                            final String aggregateName,
                                            final List<CodeGenerationParameter> stateFields,
                                            final List<CodeGenerationParameter> valueObjects) {
    return new TestDataValueGenerator(dataSetSize, aggregateName, valuePrefix, stateFields, valueObjects);
  }

  private TestDataValueGenerator(final int dataSetSize,
                                 final String valuePrefix,
                                 final String aggregateName,
                                 final List<CodeGenerationParameter> stateFields,
                                 final List<CodeGenerationParameter> valueObjects) {

    this.dataSetSize = dataSetSize;
    this.valuePrefix = valuePrefix;
    this.aggregateName = aggregateName;
    this.stateFields.addAll(stateFields);
    this.valueObjects.addAll(valueObjects);
  }

  public TestDataValues generate() {
    IntStream.range(1, dataSetSize + 1).forEach(this::generateValues);
    return generatedValues;
  }

  private void generateValues(final int dataIndex) {
    resetCurrentValues();
    stateFields.forEach(field -> this.generateValue(dataIndex, new String(), field));
  }

  private void generateValue(final int dataIndex, final String path, final CodeGenerationParameter field) {
    if (ValueObjectDetail.isValueObject(field)) {
      generateForValueObjectFields(dataIndex, path, field);
    } else {
      generateScalarFieldAssignment(dataIndex, path, field);
    }
  }

  private void generateForValueObjectFields(final int dataIndex, final String path, final CodeGenerationParameter field) {
    final String fieldType =
            field.retrieveRelatedValue(FIELD_TYPE);

    final CodeGenerationParameter valueObject =
            ValueObjectDetail.valueObjectOf(fieldType, valueObjects.stream());

    final String currentPath = resolvePath(path, field);

    final Consumer<CodeGenerationParameter> valueObjectFieldAssignment =
            valueObjectField -> generateValue(dataIndex, currentPath, valueObjectField);

    valueObject.retrieveAllRelated(VALUE_OBJECT_FIELD).forEach(valueObjectFieldAssignment);
  }

  private void generateScalarFieldAssignment(final int dataIndex, final String path, final CodeGenerationParameter field) {
    final String currentPath = resolvePath(path, field);
    if (field.value.equalsIgnoreCase("id")) {
      generatedValues.add(dataIndex, currentPath, quoteValue(dataIndex));
    } else if (FieldDetail.hasNumericType(field)) {
      generatedValues.add(dataIndex, currentPath, currentNumericValue);
      alternateNumericValue();
    } else if (FieldDetail.hasBooleanType(field)) {
      generatedValues.add(dataIndex, currentPath, currentBooleanValue);
      alternateBooleanValue();
    } else if (FieldDetail.hasStringType(field)) {
      final String alias = valuePrefix.toLowerCase();
      final String ordinalIndex = NumberFormat.toOrdinal(dataIndex);
      final String hyphenatedPath = currentPath.replaceAll("\\.", "-");
      final String value = String.format("%s-%s-%s", ordinalIndex, alias, hyphenatedPath);
      generatedValues.add(dataIndex, currentPath, quoteValue(value));
    } else {
      throw new IllegalArgumentException(field.value + " " + field.retrieveRelatedValue(FIELD_TYPE) + " is not Scalar");
    }
  }

  private String quoteValue(final Object value) {
    return "\"" + value + "\"";
  }

  private String resolvePath(final String path, final CodeGenerationParameter field) {
    return path.isEmpty() ? field.value : path + "." + field.value;
  }

  private void alternateNumericValue() {
    this.currentNumericValue++;
  }

  private void alternateBooleanValue() {
    this.currentBooleanValue = !currentBooleanValue;
  }

  private void resetCurrentValues() {
    this.currentNumericValue = 1;
    this.currentBooleanValue = true;
  }

  public static class TestDataValues {

    private final Map<Integer, List<TestDataValue>> generatedValues = new HashMap<>();

    private class TestDataValue {
      public final String fieldPath;
      public final String value;

      private TestDataValue(final String fieldPath, final String value) {
        this.fieldPath = fieldPath;
        this.value = value;
      }

      public boolean hasPath(final String fieldPath) {
        return this.fieldPath.equals(fieldPath);
      }
    }

    public void add(final int dataIndex, final String path, final Object value) {
      final TestDataValue newValue = new TestDataValue(path, value.toString());
      generatedValues.computeIfAbsent(dataIndex, v -> new ArrayList<>()).add(newValue);
    }

    public String retrieve(final int dataIndex, final String path) {
      return generatedValues.get(dataIndex).stream().filter(value -> value.hasPath(path))
              .findFirst().orElseThrow(() -> new IllegalArgumentException("Unable to find value for " + path)).value;
    }

    public String retrieve(final int dataIndex, final String variableName, final String path) {
      final String reducedPath =
              path.substring(variableName.length() + 1, path.length());

      return retrieve(dataIndex, reducedPath);
    }
  }
}
