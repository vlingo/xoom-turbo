// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.codegen.parameter;

import io.vlingo.xoom.turbo.codegen.parameter.CodeGenerationParameters.RetrievalLevel;

import java.util.function.Function;
import java.util.stream.Stream;

public class CodeGenerationParameter {

  public final ParameterLabel label;
  public final String value;
  private CodeGenerationParameter parent;
  private final CodeGenerationParameters relatedParameters;

  public static CodeGenerationParameter of(final ParameterLabel label) {
    return of(label, label.toString());
  }

  public static CodeGenerationParameter of(final ParameterLabel label, final Object value) {
    return of(label, value.toString());
  }

  public static CodeGenerationParameter of(final ParameterLabel label, final String value) {
    return new CodeGenerationParameter(label, value);
  }

  private CodeGenerationParameter(final ParameterLabel label, final String value) {
    this(label, value, null, CodeGenerationParameters.empty());
  }

  private CodeGenerationParameter(final ParameterLabel label,
                                  final String value,
                                  final CodeGenerationParameter parent,
                                  final CodeGenerationParameters relatedParameters) {
    this.label = label;
    this.value = value;
    this.parent = parent;
    this.relatedParameters = relatedParameters;
  }

  public CodeGenerationParameter relate(final ParameterLabel label, final Object value) {
    return relate(label, value.toString());
  }

  public CodeGenerationParameter relate(final ParameterLabel label, final String value) {
    return this.relate(CodeGenerationParameter.of(label, value));
  }

  public CodeGenerationParameter relate(final CodeGenerationParameter... relatedParameters) {
    Stream.of(relatedParameters).forEach(relatedParameter -> {
      relatedParameter.ownedBy(this);
      this.relatedParameters.add(relatedParameter);
    });
    return this;
  }

  public CodeGenerationParameter retrieveOneRelated(final ParameterLabel label) {
    return this.relatedParameters.retrieveOne(label);
  }

  public Stream<CodeGenerationParameter> retrieveAllRelated(final ParameterLabel label) {
    return retrieveAllRelated(label, RetrievalLevel.SUPERFICIAL);
  }

  public Stream<CodeGenerationParameter> retrieveAllRelated(final ParameterLabel label, final RetrievalLevel levels) {
    return relatedParameters.retrieveAll(label, levels);
  }

  public Stream<CodeGenerationParameter> retrieveAllRelated() {
    return relatedParameters.list().stream();
  }

  public CodeGenerationParameter parent() {
    return parent;
  }

  public CodeGenerationParameter parent(final ParameterLabel label) {
    if (!hasParent()) {
      throw new UnsupportedOperationException("Orphan parameter");
    }

    CodeGenerationParameter matchedParent = parent;

    while (matchedParent != null) {
      if (matchedParent.isLabeled(label)) {
        return matchedParent;
      }
      matchedParent = matchedParent.parent();
    }

    throw new UnsupportedOperationException("Orphan parameter");
  }

  public boolean hasParent() {
    return parent != null;
  }

  private void ownedBy(final CodeGenerationParameter parent) {
    this.parent = parent;
  }

  public String retrieveRelatedValue(final ParameterLabel label) {
    return retrieveRelatedValue(label, value -> value);
  }

  public <T> T retrieveRelatedValue(final ParameterLabel label, final Function<String, T> mapper) {
    return mapper.apply(retrieveOneRelated(label).value);
  }

  public boolean isLabeled(final ParameterLabel label) {
    return this.label.equals(label);
  }

  public Stream<CodeGenerationParameter> relatedParametersAsStream() {
    return relatedParameters.list().stream();
  }

  public boolean hasAny(final ParameterLabel label) {
    return isLabeled(label) || relatedParameters.list().stream().anyMatch(parameter -> parameter.isLabeled(label) && parameter.value != null && !parameter.value.isEmpty());
  }

  public void convertValuesSyntax(final ParameterLabel label, final Function<String, String> formatter) {
    relatedParameters.applySyntaxConverter(label, formatter);
  }

  protected CodeGenerationParameter formatValue(final Function<String, String> formatter) {
    return new CodeGenerationParameter(label, formatter.apply(value), parent, relatedParameters);
  }

}
