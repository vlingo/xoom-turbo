// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.parameter;

import java.util.stream.Stream;

public class CodeGenerationParameter {

    public final Label label;
    public final String value;
    private final CodeGenerationParameters relatedParameters;

    public static CodeGenerationParameter of(final Label label, final Object value) {
        return of(label, value.toString());
    }

    public static CodeGenerationParameter of(final Label label, final String value) {
        return new CodeGenerationParameter(label, value);
    }

    private CodeGenerationParameter(final Label label, final String value) {
        this.label = label;
        this.value = value;
        this.relatedParameters = CodeGenerationParameters.empty();
    }

    public CodeGenerationParameter relate(final Label label, final Object value) {
        return relate(label, value.toString());
    }

    public CodeGenerationParameter relate(final Label label, final String value) {
        this.relatedParameters.add(label, value);
        return this;
    }

    public CodeGenerationParameter relate(final CodeGenerationParameter relatedParameter) {
        this.relatedParameters.add(relatedParameter);
        return this;
    }

    public CodeGenerationParameter relatedParameterOf(final Label label) {
        return this.relatedParameters.retrieve(label);
    }

    public Stream<CodeGenerationParameter> retrieveAll(final Label label) {
        return relatedParameters.retrieveAll(label);
    }

    public String relatedParameterValueOf(final Label label) {
        return relatedParameterOf(label).value;
    }

    public boolean has(final Label label) {
        return this.label.equals(label);
    }

    public boolean hasAny(final Label label) {
        return has(label) || relatedParameters.list().stream().anyMatch(parameter -> parameter.has(label));
    }
}
