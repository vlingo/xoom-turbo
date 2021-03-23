// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.dataobject;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.formatting.Formatters;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.codegen.template.model.MethodScope.INSTANCE;
import static io.vlingo.xoom.codegen.template.model.MethodScope.STATIC;

public class StaticFactoryMethod {

  private final String parameters;
  private final String dataObjectName;
  private final String constructorInvocation;

  public static List<StaticFactoryMethod> from(final CodeGenerationParameter parent) {
    return Arrays.asList(new StaticFactoryMethod(parent, Type.SINGLE_ARG),
            new StaticFactoryMethod(parent, Type.ALL_ARGS));
  }

  private StaticFactoryMethod(final CodeGenerationParameter parent,
                              final Type staticFactoryMethodType) {
    this.dataObjectName = TemplateStandard.DATA_OBJECT.resolveClassname(parent.value);
    this.parameters = resolveMethodParameters(parent, staticFactoryMethodType);
    this.constructorInvocation = resolveConstructorInvocation(parent, staticFactoryMethodType);
  }

  private String resolveMethodParameters(final CodeGenerationParameter parent,
                                         final Type staticFactoryMethodType) {
    final String parameterPattern = "final %s %s";

    if(staticFactoryMethodType.isSingleArg()) {
      return String.format(parameterPattern, resolveCarrierName(parent), Introspector.decapitalize(parent.value));
    }

    return Formatters.Arguments.DATA_OBJECT_STATIC_FACTORY_METHOD_PARAMETERS.format(parent);
  }

  private String resolveConstructorInvocation(final CodeGenerationParameter parent,
                                              final Type staticFactoryMethodType) {
    if(staticFactoryMethodType.isSingleArg()) {
      return String.format("from(%s)", Formatters.Arguments.DATA_OBJECT_CONSTRUCTOR_INVOCATION.format(parent, STATIC));
    }
    return String.format("new %s(%s)", dataObjectName, Formatters.Arguments.DATA_OBJECT_CONSTRUCTOR_INVOCATION.format(parent, INSTANCE));
  }

  private String resolveCarrierName(final CodeGenerationParameter parent) {
    if(parent.isLabeled(Label.AGGREGATE)) {
      return TemplateStandard.AGGREGATE_STATE.resolveClassname(parent.value);
    }
    if(parent.isLabeled(Label.VALUE_OBJECT)) {
      return parent.value;
    }
    throw new IllegalArgumentException("Unable to resolve carrier name from " + parent.label);
  }

  public String getParameters() {
    return parameters;
  }

  public String getDataObjectName() {
    return dataObjectName;
  }

  public String getConstructorInvocation() {
    return constructorInvocation;
  }

  private static enum Type {
    SINGLE_ARG,
    ALL_ARGS;

    boolean isSingleArg() {
      return equals(SINGLE_ARG);
    }

    boolean isAllArgs() {
      return equals(ALL_ARGS);
    }
  }

}
