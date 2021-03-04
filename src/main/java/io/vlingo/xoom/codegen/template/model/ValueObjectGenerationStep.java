// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;
import io.vlingo.xoom.codegen.template.model.valueobject.ValueObjectTemplateData;

import java.util.List;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.VALUE_OBJECT;

public class ValueObjectGenerationStep extends TemplateProcessingStep {

  @Override
  protected List<TemplateData> buildTemplatesData(CodeGenerationContext context) {
    final String basePackage = context.parameters().retrieveValue(Label.PACKAGE);
    final Language language = context.parameters().retrieveValue(Label.LANGUAGE, Language::valueOf);
    final Stream<CodeGenerationParameter> aggregates = context.parameters().retrieveAll(Label.AGGREGATE);
    final Stream<CodeGenerationParameter> valueObjects = context.parameters().retrieveAll(Label.VALUE_OBJECT);
    return ValueObjectTemplateData.from(basePackage, language, aggregates, valueObjects);
  }

  @Override
  public boolean shouldProcess(final CodeGenerationContext context) {
    return context.hasParameter(VALUE_OBJECT);
  }

}
