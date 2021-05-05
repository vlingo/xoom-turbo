// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.template;

import io.vlingo.xoom.turbo.annotation.codegen.template.TemplateParameter;
import io.vlingo.xoom.turbo.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.turbo.codegen.parameter.ImportParameter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.vlingo.xoom.turbo.annotation.codegen.template.TemplateParameter.IMPORTS;
import static io.vlingo.xoom.turbo.annotation.codegen.template.TemplateParameter.PACKAGE_NAME;

public class TemplateParameters {

  private final Map<String, Obj
  ect> parameters = new HashMap<>();
  public static final String PRODUCTION_CODE_KEY = "productionCode";

  private TemplateParameters() {
    parameters.put(PRODUCTION_CODE_KEY, true);
  }

  public static TemplateParameters empty() {
    return new TemplateParameters();
  }

  public static TemplateParameters with(final ParameterKey key, final Object value) {
    return new TemplateParameters().and(key, value);
  }

  public TemplateParameters and(final ParameterKey key, final Object value) {
    this.parameters.put(key.value(), value);
    return this;
  }

  public TemplateParameters andResolve(final ParameterKey key, final Function<TemplateParameters, Object> resolver) {
    this.parameters.put(key.value(), resolver.apply(this));
    return this;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public TemplateParameters addImport(final String qualifiedClassName) {
    if (this.find(TemplateParameter.IMPORTS) == null) {
      this.and(TemplateParameter.IMPORTS, new HashSet<ImportParameter>());
    }
    if (validateImport(qualifiedClassName)) {
      this.<Set>find(TemplateParameter.IMPORTS).add(new ImportParameter(qualifiedClassName.trim()));
    }
    return this;
  }

  private boolean validateImport(final String qualifiedClassName) {
    if (qualifiedClassName == null || qualifiedClassName.trim().isEmpty()) {
      return false;
    }

    final String classPackage = CodeElementFormatter.packageOf(qualifiedClassName);

    return !find(PACKAGE_NAME).equals(classPackage);
  }

  public TemplateParameters addImports(final Set<String> qualifiedClassNames) {
    qualifiedClassNames.forEach(this::addImport);
    return this;
  }

  public void convertImportSyntax(final Function<String, String> syntaxResolver) {
    if (hasImports()) {
      final Set<ImportParameter> imports = this.find(IMPORTS);
      final Set<String> resolvedImports =
              imports.stream().map(ImportParameter::getQualifiedClassName)
                      .map(syntaxResolver).collect(Collectors.toSet());
      remove(IMPORTS);
      addImports(resolvedImports);
    }
  }

  @SuppressWarnings("unchecked")
  public boolean hasImport(final String qualifiedName) {
    return ((Set<ImportParameter>) find(IMPORTS)).stream()
            .anyMatch(imp -> imp.matchClass(qualifiedName));
  }

  @SuppressWarnings("unchecked")
  public <T> T find(final ParameterKey parameter) {
    return (T) this.parameters.get(parameter.value());
  }

  public <T> T find(final ParameterKey parameter, final T defaultValue) {
    if (!this.parameters.containsKey(parameter.value())) {
      return defaultValue;
    }
    return find(parameter);
  }

  private void remove(final ParameterKey key) {
    this.parameters.remove(key.value());
  }

  public Map<String, Object> map() {
    return parameters;
  }

  public boolean has(final ParameterKey parameter) {
    return parameters.containsKey(parameter.value()) &&
            parameters.get(parameter.value()) != null &&
            !parameters.get(parameter.value()).toString().trim().isEmpty();
  }

  public boolean hasValue(final ParameterKey parameter, final String value) {
    return has(parameter) && this.parameters.get(parameter.value()).equals(value);
  }

  private boolean hasImports() {
    return this.parameters.containsKey(IMPORTS.key);
  }
}
