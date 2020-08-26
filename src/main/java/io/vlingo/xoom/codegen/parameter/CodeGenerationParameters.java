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

import static io.vlingo.xoom.codegen.parameter.Category.GENERAL;

public class CodeGenerationParameters {

    private final List<CodeGenerationParameter> parameters = new ArrayList<>();

    public static CodeGenerationParameters from(final Label label, final String value) {
        return new CodeGenerationParameters(Arrays.asList(new CodeGenerationParameter(GENERAL, label, value)));
    }

    public static CodeGenerationParameters empty() {
        return new CodeGenerationParameters(new ArrayList<>());
    }

    private CodeGenerationParameters(final List<CodeGenerationParameter> parameters) {
        this.parameters.addAll(parameters);
    }

    public void add(final Label label, final String value) {
        add(GENERAL, Parent.none(), label, value);
    }

    public void add(final Category category,
                    final Parent parent,
                    final Label label,
                    final String value) {
        this.parameters.add(new CodeGenerationParameter(category, parent, label, value));
    }

    public void addAll(final CodeGenerationParameters parameters) {
        addAll(parameters.list());
    }

    public void addAll(final List<CodeGenerationParameter> parameters) {
        this.parameters.addAll(parameters);
    }

    public void addAll(final Map<Label, String> parameterEntries) {
        final Function<Entry<Label, String>, CodeGenerationParameter> mapper =
                entry -> new CodeGenerationParameter(GENERAL, entry.getKey(), entry.getValue());

        final List<CodeGenerationParameter> parameters =
                parameterEntries.entrySet().stream().map(mapper).collect(Collectors.toList());

        this.parameters.addAll(parameters);
    }

    public String retrieveValue(final Label label) {
        return retrieveValue(GENERAL, Parent.none(), label);
    }

    public String retrieveValue(final Category category, final Parent parent, final Label label) {
        return parameters.stream()
                .filter(param -> param.has(category, parent, label)).findFirst()
                .orElse(new CodeGenerationParameter(category, label, "")).value;
    }

    protected List<CodeGenerationParameter> list() {
        return Collections.unmodifiableList(parameters);
    }

}
