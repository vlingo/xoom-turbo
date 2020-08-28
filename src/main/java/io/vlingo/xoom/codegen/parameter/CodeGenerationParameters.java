// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.parameter;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CodeGenerationParameters {

    private final List<CodeGenerationParameter> parameters = new ArrayList<>();

    public static CodeGenerationParameters from(final Label label, final String value) {
        return new CodeGenerationParameters(Arrays.asList(new CodeGenerationParameter(label, value)));
    }

    public static CodeGenerationParameters empty() {
        return new CodeGenerationParameters(new ArrayList<>());
    }

    private CodeGenerationParameters(final List<CodeGenerationParameter> parameters) {
        this.parameters.addAll(parameters);
    }

    public CodeGenerationParameters add(final Label label, final String value) {
        return add(new CodeGenerationParameter(label, value));
    }


    public CodeGenerationParameters add(final CodeGenerationParameter parameter) {
        this.parameters.add(parameter);
        return this;
    }

    public void addAll(final Map<Label, String> parameterEntries) {
        final Function<Entry<Label, String>, CodeGenerationParameter> mapper =
                entry -> new CodeGenerationParameter(entry.getKey(), entry.getValue());

        addAll(parameterEntries.entrySet().stream().map(mapper).collect(Collectors.toList()));
    }

    public void addAll(final CodeGenerationParameters parameters) {
        addAll(parameters.list());
    }

    public void addAll(final List<CodeGenerationParameter> parameters) {
        this.parameters.addAll(parameters);
    }

    public String retrieveValue(final Label label) {
        return retrieve(label).value;
    }

    public CodeGenerationParameter retrieve(final Label label) {
        return parameters.stream()
                .filter(param -> param.has(label)).findFirst()
                .orElse(new CodeGenerationParameter(label, ""));
    }

    protected List<CodeGenerationParameter> list() {
        return Collections.unmodifiableList(parameters);
    }


}
