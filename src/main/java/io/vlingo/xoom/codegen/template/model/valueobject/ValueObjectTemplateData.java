// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model.valueobject;

import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.formatting.Formatters;
import io.vlingo.xoom.codegen.template.model.formatting.Formatters.Fields.Style;

import java.util.List;
import java.util.stream.Collectors;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.model.valueobject.ValueObjectDetail.resolvePackage;

public class ValueObjectTemplateData extends TemplateData {

  private final TemplateParameters parameters;

  public static List<TemplateData> from(final CodeGenerationParameters parameters) {
    final String basePackage =
            parameters.retrieveValue(Label.PACKAGE);

    final Language language =
            parameters.retrieveValue(Label.LANGUAGE, Language::valueOf);

    final List<CodeGenerationParameter> aggregates =
            parameters.retrieveAll(Label.AGGREGATE).collect(Collectors.toList());

    return parameters.retrieveAll(Label.VALUE_OBJECT).map(valueObject ->
            new ValueObjectTemplateData(basePackage, language, valueObject, aggregates))
            .collect(Collectors.toList());
  }

  private ValueObjectTemplateData(final String basePackage,
                                  final Language language,
                                  final CodeGenerationParameter valueObject,
                                  final List<CodeGenerationParameter> aggregates) {
    this.parameters =
            TemplateParameters.with(PACKAGE_NAME, resolvePackage(basePackage, valueObject, aggregates))
                    .and(VALUE_OBJECT_NAME, valueObject.value)
                    .and(CONSTRUCTOR_PARAMETERS, Formatters.Arguments.SIGNATURE_DECLARATION.format(valueObject))
                    .and(CONSTRUCTOR_INVOCATION_PARAMETERS, Formatters.Arguments.VALUE_OBJECT_CONSTRUCTOR_INVOCATION.format(valueObject))
                    .and(MEMBERS, Formatters.Fields.format(Style.MEMBER_DECLARATION, language, valueObject))
                    .and(MEMBERS_ASSIGNMENT, Formatters.Fields.format(Style.ASSIGNMENT, language, valueObject));
  }

  @Override
  public TemplateParameters parameters() {
    return parameters;
  }

  @Override
  public TemplateStandard standard() {
    return TemplateStandard.VALUE_OBJECT;
  }

  @Override
  public String filename() {
    return standard().resolveFilename(parameters.find(VALUE_OBJECT_NAME), parameters);
  }
}
