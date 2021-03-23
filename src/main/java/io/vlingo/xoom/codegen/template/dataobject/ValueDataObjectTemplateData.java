// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.dataobject;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.language.Language;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.model.formatting.Formatters;
import io.vlingo.xoom.codegen.template.model.formatting.Formatters.Fields.Style;
import io.vlingo.xoom.codegen.template.model.valueobject.ValueObjectDetail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.model.formatting.Formatters.Arguments.SIGNATURE_DECLARATION;

public class ValueDataObjectTemplateData extends TemplateData {

  private final String valueObjectName;
  private final TemplateParameters parameters;

  public static List<TemplateData> from(final String basePackage,
                                        final Language language,
                                        final Stream<CodeGenerationParameter> valueObjects,
                                        final List<Content> contents) {
    final String packageName = String.format("%s.%s", basePackage, "infrastructure");

    final Function<CodeGenerationParameter, TemplateData> mapper =
            valueObject -> new ValueDataObjectTemplateData(packageName, language, valueObject, contents);

    return valueObjects.map(mapper).collect(Collectors.toList());
  }

  private ValueDataObjectTemplateData(final String packageName,
                                      final Language language,
                                      final CodeGenerationParameter valueObject,
                                      final List<Content> contents) {
    this.valueObjectName = valueObject.value;
    this.parameters =
            TemplateParameters.with(PACKAGE_NAME, packageName)
                    .and(DATA_VALUE_OBJECT_NAME, standard().resolveClassname(valueObjectName))
                    .and(CONSTRUCTOR_PARAMETERS, SIGNATURE_DECLARATION.format(valueObject))
                    .and(STATIC_FACTORY_METHODS, StaticFactoryMethod.from(valueObject))
                    .and(MEMBERS, Formatters.Fields.format(Style.DATA_OBJECT_MEMBER_DECLARATION, language, valueObject))
                    .and(MEMBERS_ASSIGNMENT, Formatters.Fields.format(Style.DATA_VALUE_OBJECT_ASSIGNMENT, language, valueObject))
                    .addImports(resolveImports(valueObject, contents));
  }

  private Set<String> resolveImports(final CodeGenerationParameter valueObject,
                                     final List<Content> contents) {
    final Set<String> imports = new HashSet<>();
    imports.addAll(ValueObjectDetail.resolveImports(contents, valueObject.retrieveAllRelated(Label.VALUE_OBJECT_FIELD)));
    if(imports.isEmpty()) {
      imports.add(ValueObjectDetail.resolveImport(valueObject.value, contents));
    }
    return imports;
  }

  @Override
  public TemplateParameters parameters() {
    return parameters;
  }

  @Override
  public TemplateStandard standard() {
    return TemplateStandard.DATA_OBJECT;
  }

  @Override
  public String filename() {
    return standard().resolveFilename(valueObjectName, parameters);
  }
}
