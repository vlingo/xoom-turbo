// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen;

import com.google.common.collect.Maps;
import io.vlingo.xoom.codegen.storage.DatabaseType;
import io.vlingo.xoom.codegen.storage.ModelClassification;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import static io.vlingo.xoom.codegen.CodeGenerationParameter.*;

public class CodeGenerationContext {

    private final List<Content> contents = new ArrayList<>();
    private final Map<CodeGenerationParameter, String> parameters = new HashMap<>();

    public static CodeGenerationContext empty() {
        return new CodeGenerationContext();
    }

    public static CodeGenerationContext with(final Map<CodeGenerationParameter, String> parameters) {
        return new CodeGenerationContext().onParameters(parameters);
    }

    public CodeGenerationContext onParameters(final Map<CodeGenerationParameter, String> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    public CodeGenerationContext with(final CodeGenerationParameter parameter, final String value) {
        this.parameters.put(parameter, value);
        return this;
    }

    public void addContent(final Object subject, final File file, final String text) {
        this.contents.add(Content.with(subject, file, text));
    }

    public <T> T parameterOf(final CodeGenerationParameter codeGenerationParameter) {
        return (T) parameterOf(codeGenerationParameter, value -> value);
    }

    public <T> T parameterOf(final CodeGenerationParameter codeGenerationParameter, final Function<String, T> mapper) {
        final String value = parameters.get(codeGenerationParameter);
        return (T) mapper.apply(value);
    }

    public List<Content> contents() {
        return contents;
    }

    public Map<ModelClassification, DatabaseType> databases() {
        return Maps.immutableEnumMap(
                new HashMap<ModelClassification, DatabaseType>(){{
                    put(ModelClassification.SINGLE, parameterOf(DATABASE, DatabaseType::valueOf));
                    put(ModelClassification.COMMAND, parameterOf(COMMAND_MODEL_DATABASE, DatabaseType::valueOf));
                    put(ModelClassification.QUERY, parameterOf(QUERY_MODEL_DATABASE, DatabaseType::valueOf));
                }}
        );
    }

    public String projectPath() {
        final String targetFolder = parameterOf(TARGET_FOLDER);
        final String appName = parameterOf(APPLICATION_NAME);
        return Paths.get(targetFolder, appName).toString();
    }

    public boolean hasParameter(final CodeGenerationParameter codeGenerationParameter) {
        return this.parameterOf(codeGenerationParameter) != null &&
                !this.<String>parameterOf(codeGenerationParameter).trim().isEmpty();
    }

}
