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
import java.util.stream.Stream;

public class CodeGenerationParameters {

    private final List<CodeGenerationParameter> parameters = new ArrayList<>();

    public static CodeGenerationParameters from(final Label label, final Object value) {
        return from(label, value.toString());
    }

    public static CodeGenerationParameters from(final Label label, final String value) {
        return from(CodeGenerationParameter.of(label, value));
    }

    public static CodeGenerationParameters from(final CodeGenerationParameter ...codeGenerationParameters) {
        return new CodeGenerationParameters(Arrays.asList(codeGenerationParameters));
    }

    public static CodeGenerationParameters empty() {
        return new CodeGenerationParameters(new ArrayList<>());
    }

    private CodeGenerationParameters(final List<CodeGenerationParameter> parameters) {
        this.parameters.addAll(parameters);
    }

    public CodeGenerationParameters add(final Label label, final Object value) {
        return add(label, value.toString());
    }

    public CodeGenerationParameters add(final Label label, final String value) {
        return add(CodeGenerationParameter.of(label, value));
    }

    public CodeGenerationParameters add(final CodeGenerationParameter parameter) {
        this.parameters.add(parameter);
        return this;
    }

    public CodeGenerationParameters child(final CodeGenerationParameter parameter) {
        if(this.parameters.isEmpty()) {
            throw new UnsupportedOperationException("Unable to add a parameter child without a parent");
        }
        this.parameters.get(this.parameters.size() - 1).relate(parameter);
        return this;
    }

    public void addAll(final Map<Label, String> parameterEntries) {
        final Function<Entry<Label, String>, CodeGenerationParameter> mapper =
                entry -> CodeGenerationParameter.of(entry.getKey(), entry.getValue());

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
                .orElse(CodeGenerationParameter.of(label, ""));
    }

    protected List<CodeGenerationParameter> list() {
        return Collections.unmodifiableList(parameters);
    }

    public Stream<CodeGenerationParameter> retrieveAll(final Label label) {
        return parameters.stream().filter(param -> param.has(label));
    }

    public boolean isEmpty() {
        return parameters.isEmpty();
    }

}
